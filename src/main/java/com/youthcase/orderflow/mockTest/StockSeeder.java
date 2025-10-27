package com.youthcase.orderflow.mockTest;


import com.youthcase.orderflow.gr.domain.Lot;
import com.youthcase.orderflow.gr.repository.LotRepository;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.product.domain.StorageMethod;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import com.youthcase.orderflow.master.warehouse.repository.WarehouseRepository;
import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.repository.STKRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@Profile({"dev","local"})
@Order(10) // ⚠️ Warehouse/Product/Lot/(GR) 시더보다 뒤
@RequiredArgsConstructor
public class StockSeeder implements CommandLineRunner {

    private final STKRepository stkRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final LotRepository lotRepository;

    @Override
    @Transactional
    public void run(String... args) {

        Warehouse wh = warehouseRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("창고가 먼저 시드 되어 있어야 합니다."));
        String whId = wh.getWarehouseId();

        // 2) GTIN → 여러 로트 계획(유통기한/수량/위치/보관구역)
        Map<String, List<Plan>> plan = new LinkedHashMap<>();
        plan.put("8801115115809", List.of(
                p(1L, d(30), 20, StorageMethod.ROOM_TEMP, "A", 1),
                p(2L, d(60), 10, StorageMethod.ROOM_TEMP, "A", 2)
        ));
        plan.put("8809929360583", List.of(
                p(3L, d(90), 25, StorageMethod.FROZEN, "F", 1)
        ));
        plan.put("8801128244077", List.of(
                p(4L, d(14), 15, StorageMethod.COLD, "C", 3),
                p(5L, d(7),   8,  StorageMethod.COLD, "C", 4) // 임박 테스트용
        ));

        int created =0, skipped =0;

        // 3) 시딩 실행
        for (var e : plan.entrySet()) {
            String gtin = e.getKey();
            Product product = productRepository.findById(gtin)
                    .orElseThrow(() -> new IllegalStateException("Product 미존재: " + gtin));

            for (Plan sp : e.getValue()) {
                Lot lot = lotRepository.findById(sp.lotId())
                        .orElseThrow(() -> new IllegalStateException("Lot 미존재: id=" + sp.lotId()));



                // (선택) 계획된 exp와 Lot.expDate가 다르면 로깅/보정
                if (lot.getExpDate() != null && !lot.getExpDate().equals(sp.expDate())) {
                    log.debug("[StockSeeder] 예정 exp({}) ≠ Lot.exp({}) : lotId={}",
                            sp.expDate(), lot.getExpDate(), lot.getLotId());
                    // 필요하면 lot.setExpDate(sp.expDate());
                }

                // GR 연결 여부 판단: Lot -> GoodsReceiptItem -> Header
                var gri = lot.getGoodsReceiptItem();
                var header = (gri != null) ? gri.getHeader() : null;

                if (header != null) {
                    // ✅ 확정 재고: 중복 체크 (warehouse + headerId + lotId)
                    boolean exists = stkRepository
                            .existsByWarehouse_WarehouseIdAndGoodsReceipt_IdAndLot_LotId(
                                    whId, header.getId(), lot.getLotId());
                    if (exists) { skipped++; continue; }

                    // product 일치성(방어)
                    Product lotProduct = gri.getProduct();
                    if (lotProduct != null && !lotProduct.getGtin().equals(product.getGtin())) {
                        log.warn("[StockSeeder] GTIN 불일치: plan={} lot={} (lotId={})",
                                product.getGtin(), lotProduct.getGtin(), lot.getLotId());
                    }

                    STK s = STK.builder()
                            .warehouse(wh)
                            .goodsReceipt(header)
                            .product(product)
                            .lot(lot)
                            .quantity((long) sp.qty())          // 확정재고: 계획 수량 사용(혹은 lot.getQty())
                            .hasExpirationDate(lot.getExpDate() != null)
                            .status("ACTIVE")
                            .location(formatLocation(sp.sm(), sp.aisle(), sp.bay()))
                            .lastUpdatedAt(LocalDateTime.now())
                            .build();
                    stkRepository.save(s);
                    created++;

                } else {
                    // ✅ 임시 재고: GR NULL 기준 중복 체크
                    Optional<STK> existsTemp = stkRepository
                            .findByWarehouse_WarehouseIdAndProduct_GtinAndLot_LotIdAndGoodsReceiptIsNull(
                                    whId, product.getGtin(), lot.getLotId());
                    if (existsTemp.isPresent()) { skipped++; continue; }

                    STK s = STK.builder()
                            .warehouse(wh)
                            .goodsReceipt(null)
                            .product(product)
                            .lot(lot)
                            .quantity(Math.max(1L, sp.qty() / 10)) // 샘플로 소량
                            .hasExpirationDate(lot.getExpDate() != null)
                            .status("ACTIVE")
                            .location(formatLocation(sp.sm(), sp.aisle(), sp.bay()))
                            .lastUpdatedAt(LocalDateTime.now())
                            .build();
                    stkRepository.save(s);
                    created++;
                }
            }
        }

        log.info("[StockSeeder] 완료: 생성 {}건, 스킵 {}건", created, skipped);
    }

    // ===== 헬퍼 =====
    private static LocalDate d(int plusDays) { return LocalDate.now().plusDays(plusDays); }
    private static Plan p(Long lotId, LocalDate exp, int qty, StorageMethod sm, String aisle, int bay) {
        return new Plan(lotId, exp, qty, sm, aisle, bay);
    }
    private record Plan(long lotId, LocalDate expDate, int qty, StorageMethod sm, String aisle, int bay) {}

    private String formatLocation(StorageMethod sm, String aisle, int bay) {
        // StorageMethod에 getPrefix() 추가해둔 가정: ROOM/CHILLED/FROZEN/OTHER
        String prefix = sm.getPrefix() ;
        return String.format("%s-%s-%02d", prefix, aisle, bay);
    }
}
