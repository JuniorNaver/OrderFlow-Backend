package com.youthcase.orderflow.mockTest.stk;

import com.youthcase.orderflow.gr.domain.Lot;
import com.youthcase.orderflow.gr.repository.LotRepository;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import com.youthcase.orderflow.master.warehouse.repository.WarehouseRepository;
import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.domain.StockStatus;
import com.youthcase.orderflow.stk.repository.STKRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * 📦 StockSeeder
 * --------------------------------------------------------
 * - LOT 기반으로 MM_STOCK 데이터 생성
 * - 각 LOT은 Warehouse 및 Product와 연결됨
 * - 수량 및 상태는 LOT 기준으로 결정
 * --------------------------------------------------------
 */
@Slf4j
@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class StockSeeder implements CommandLineRunner {

    private final LotRepository lotRepository;
    private final WarehouseRepository warehouseRepository;
    private final STKRepository stkRepository;

    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) {
        log.info("📦 [StockSeeder] Start creating MM_STOCK based on LOT...");

        List<Warehouse> warehouses = warehouseRepository.findAll();
        if (warehouses.isEmpty()) {
            log.warn("⚠️ [StockSeeder] No warehouse found — skipping stock creation.");
            return;
        }

        List<Lot> lots = lotRepository.findAll();
        if (lots.isEmpty()) {
            log.warn("⚠️ [StockSeeder] No LOT found — skipping stock creation.");
            return;
        }

        int inserted = 0;

        for (Lot lot : lots) {
            Warehouse warehouse = warehouses.get(random.nextInt(warehouses.size()));

            Long qty = lot.getQty() != null ? lot.getQty() : 0L;
            if (qty <= 0) continue;

            StockStatus status = resolveStatus(lot, qty);

            STK stk = STK.builder()
                    .hasExpirationDate(lot.getExpDate() != null)
                    .quantity(qty)
                    .lastUpdatedAt(LocalDateTime.now())
                    .status(status)
                    .warehouse(warehouse)
                    .goodsReceipt(lot.getGoodsReceiptItem() != null
                            ? lot.getGoodsReceiptItem().getHeader()
                            : null)
                    .product(lot.getProduct())
                    .lot(lot)
                    .isRelocationNeeded(false)
                    .location(warehouse.getWarehouseName() + "-A01")
                    .build();

            stkRepository.save(stk);
            inserted++;
        }

        log.info("✅ [StockSeeder] Created {} STK records from {} LOTs.", inserted, lots.size());
    }

    /**
     * 📊 LOT 기준 상태 판별
     */
    private StockStatus resolveStatus(Lot lot, Long qty) {
        if (qty == 0) return StockStatus.EMPTY;

        LocalDate exp = lot.getExpDate();
        if (exp != null) {
            long remain = lot.getRemainDays();
            if (remain < 0) return StockStatus.EXPIRED;
            if (remain <= 7) return StockStatus.NEAR_EXPIRY;
        }

        return StockStatus.ACTIVE;
    }
}
