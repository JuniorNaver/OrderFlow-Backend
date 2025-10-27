package com.youthcase.orderflow.mockTest.gr;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.repository.UserRepository;
import com.youthcase.orderflow.gr.domain.GoodsReceiptHeader;
import com.youthcase.orderflow.gr.domain.GoodsReceiptItem;
import com.youthcase.orderflow.gr.repository.GoodsReceiptHeaderRepository;
import com.youthcase.orderflow.gr.repository.GoodsReceiptItemRepository;
import com.youthcase.orderflow.gr.status.GoodsReceiptStatus;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import com.youthcase.orderflow.master.warehouse.repository.WarehouseRepository;
import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POStatus;
import com.youthcase.orderflow.po.repository.POHeaderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

@Slf4j
@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class GoodsReceiptSeeder {

    private final GoodsReceiptHeaderRepository grHeaderRepository;
    private final GoodsReceiptItemRepository grItemRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final POHeaderRepository poHeaderRepository;
    private final UserRepository userRepository;

    /** GR 생성 대상 PO 상태 */
    private static final EnumSet<POStatus> TARGET_STATUSES = EnumSet.of(
            POStatus.GI,
            POStatus.PARTIAL_RECEIVED,
            POStatus.FULLY_RECEIVED,
            POStatus.CANCELED
    );

    @Transactional
    public void run(String... args) {
        log.info("📦 [GoodsReceiptSeeder] Start creating GR_HEADER and GR_ITEM...");

        User user = userRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("APP_USER 데이터가 필요합니다."));
        Warehouse warehouse = warehouseRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("WAREHOUSE 데이터가 필요합니다."));
        List<Product> products = productRepository.findAll().stream().limit(5).toList();

        // ✅ 1. 여러 상태의 POHeader 조회 (in 절 활용)
        List<POHeader> targetPOs = poHeaderRepository.findByStatusIn(TARGET_STATUSES);

        if (targetPOs.isEmpty()) {
            log.warn("⚠️ GR 생성 대상이 되는 POHeader가 없습니다. (status ∈ {})", TARGET_STATUSES);
            return;
        }

        // ✅ 2. PO별로 GR_HEADER + GR_ITEM 생성
        for (POHeader po : targetPOs) {
            if (grHeaderRepository.existsByPoHeader(po)) {
                log.warn("⚠️ PO_ID={} 이미 GR_HEADER 존재 → 스킵", po.getPoId());
                continue;
            }

            GoodsReceiptHeader header = GoodsReceiptHeader.builder()
                    .poHeader(po)
                    .user(user)
                    .status(GoodsReceiptStatus.RECEIVED)
                    .receiptDate(LocalDate.now())
                    .note("자동 생성된 입고 데이터 (" + po.getStatus() + ")")
                    .build();

            grHeaderRepository.save(header);

            for (Product product : products) {
                GoodsReceiptItem item = GoodsReceiptItem.builder()
                        .header(header)
                        .warehouse(warehouse)
                        .product(product)
                        .qty(10L)
                        .note("테스트 입고 아이템")
                        .build();
                grItemRepository.save(item);
            }

            log.info("🧩 GR_HEADER 생성 완료 → PO_ID={}, 상태={}, 아이템 {}건",
                    po.getPoId(), po.getStatus(), products.size());
        }

        log.info("✅ [GoodsReceiptSeeder] GR_HEADER + GR_ITEM 데이터 생성 완료.");
    }
}
