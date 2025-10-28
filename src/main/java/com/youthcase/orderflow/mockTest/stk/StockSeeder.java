package com.youthcase.orderflow.mockTest.stk;

import com.youthcase.orderflow.gr.domain.GoodsReceiptHeader;
import com.youthcase.orderflow.gr.domain.GoodsReceiptItem;
import com.youthcase.orderflow.gr.repository.GoodsReceiptHeaderRepository;
import com.youthcase.orderflow.gr.status.GoodsReceiptStatus;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.domain.StockStatus;
import com.youthcase.orderflow.stk.repository.STKRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 📦 StockSeeder (입고 아이템 기준)
 * --------------------------------------------------------
 * - GR_HEADER(status = RECEIVED) 기준으로 STK 생성
 * - 각 GoodsReceiptItem 단위로 MM_STOCK 생성
 * - LOT은 있으면 연결, 없으면 null
 * --------------------------------------------------------
 */
@Slf4j
@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class StockSeeder {

    private final GoodsReceiptHeaderRepository grHeaderRepository;
    private final STKRepository stkRepository;

    @Transactional
    public void run(String... args) {
        log.info("📦 [StockSeeder] Start creating STK based on GoodsReceiptItem...");

        // ✅ 1️⃣ RECEIVED 상태의 GR_HEADER만 조회
        List<GoodsReceiptHeader> receivedHeaders =
                grHeaderRepository.findByStatus(GoodsReceiptStatus.RECEIVED);

        if (receivedHeaders.isEmpty()) {
            log.warn("⚠️ [StockSeeder] No RECEIVED GR_HEADER found — skipping stock creation.");
            return;
        }

        int inserted = 0;
        int skipped = 0;

        // ✅ 2️⃣ 각 GR_HEADER → GR_ITEM 순회
        for (GoodsReceiptHeader header : receivedHeaders) {
            List<GoodsReceiptItem> items = header.getItems();
            if (items == null || items.isEmpty()) continue;

            for (GoodsReceiptItem item : items) {
                Warehouse warehouse = item.getWarehouse();
                if (warehouse == null) {
                    log.warn("⚠️ GR_ITEM({}) has no warehouse, skipping.", item.getItemNo());
                    continue;
                }

                // ✅ 중복 방지
                if (stkRepository.existsByWarehouse_WarehouseIdAndGoodsReceipt_GrHeaderIdAndLot_LotId(
                        warehouse.getWarehouseId(),
                        header.getGrHeaderId(),
                        item.getLots() != null ? item.getLots().getFirst().getLotId() : null)) {
                    skipped++;
                    log.debug("⚠️ Duplicate skipped: GR={}, ITEM={}, WAREHOUSE={}",
                            header.getGrHeaderId(), item.getItemNo(), warehouse.getWarehouseId());
                    continue;
                }

                Long qty = item.getQty() != null ? item.getQty() : 0L;
                if (qty <= 0) continue;

                StockStatus status = resolveStatus(item);

                STK stk = STK.builder()
                        .hasExpirationDate(item.getLots() != null && item.getLots().getFirst().getExpDate() != null)
                        .quantity(qty)
                        .lastUpdatedAt(LocalDateTime.now())
                        .status(status)
                        .warehouse(warehouse)
                        .goodsReceipt(header)
                        .product(item.getProduct())
                        .lot(item.getLots().getFirst())
                        .isRelocationNeeded(false)
                        .location(warehouse.getWarehouseName() + "-A01")
                        .build();

                stkRepository.save(stk);
                inserted++;
                log.info("🧩 STK created: GR_ITEM={}, QTY={}, STATUS={}",
                        item.getItemNo(), qty, status);
            }
        }

        log.info("✅ [StockSeeder] Created {} STK records ({} skipped).", inserted, skipped);
    }

    /**
     * 📊 LOT 또는 유통기한 기준으로 재고 상태 판별
     */
    private StockStatus resolveStatus(GoodsReceiptItem item) {
        if (item.getQty() == null || item.getQty() == 0)
            return StockStatus.EMPTY;

        if (item.getLots() != null && item.getLots().getFirst().getExpDate() != null) {
            LocalDate exp = item.getLots().getFirst().getExpDate();
            long remainDays = item.getLots().getFirst().getRemainDays();
            if (remainDays < 0) return StockStatus.EXPIRED;
            if (remainDays <= 7) return StockStatus.NEAR_EXPIRY;
        }

        return StockStatus.ACTIVE;
    }
}
