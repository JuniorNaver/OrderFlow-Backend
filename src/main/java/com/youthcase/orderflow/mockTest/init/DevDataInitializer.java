package com.youthcase.orderflow.mockTest.init;

import com.youthcase.orderflow.mockTest.auth.*;
import com.youthcase.orderflow.mockTest.master.*;
import com.youthcase.orderflow.mockTest.po.POSeeder;
import com.youthcase.orderflow.mockTest.pr.*;
import com.youthcase.orderflow.mockTest.gr.*;
import com.youthcase.orderflow.mockTest.stk.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 🚀 DevDataInitializer
 * --------------------------------------------------------
 * - dev/local 환경 전체 데이터 시더 실행기
 * - FK 의존 순서 기반 실행
 * - Cleaner → Auth → Master → User → Inventory → GR → LOT → STOCK
 * --------------------------------------------------------
 */
@Slf4j
@Component
@Profile({"dev", "local"})
@Order(99)
@RequiredArgsConstructor
public class DevDataInitializer implements CommandLineRunner {

    // ====== AUTH ======
    private final AuthoritySeeder authoritySeeder;
    private final RoleSeeder roleSeeder;
    private final RoleAuthoritySeeder roleAuthoritySeeder;

    // ====== MASTER ======
    private final StoreSeeder storeSeeder;
    private final WarehouseSeeder warehouseSeeder;
    private final CategorySeeder categorySeeder;
    private final ProductSeeder productSeeder;
    private final PriceSeeder priceSeeder;

    // ====== USER ======
    private final AppUserSeeder appUserSeeder;

    // ====== INVENTORY / PO / GR / LOT / STOCK ======
    private final InventorySeeder inventorySeeder;
    private final POSeeder poSeeder;
    private final GoodsReceiptSeeder goodsReceiptSeeder;
    private final LotSeeder lotSeeder;
    private final StockSeeder stockSeeder;

    @Override
    public void run(String... args) {
        log.info("🚀 [DevDataInitializer] Starting full mock data initialization...");

        try {

            // 1️⃣ AUTH (권한, 역할, 매핑)
            log.info("🔐 [1/9] Seeding authorities and roles...");
            authoritySeeder.run();
            roleSeeder.run();
            roleAuthoritySeeder.run();

            // 2️⃣ MASTER (기준 정보)
            log.info("🏪 [2/9] Seeding master data (Store, Warehouse, Category, Product, Price)...");
            storeSeeder.run();
            warehouseSeeder.run();
            categorySeeder.run();
            productSeeder.run();
            priceSeeder.run();

            // 3️⃣ USER (계정)
            log.info("👤 [3/9] Seeding app users...");
            appUserSeeder.run();

            // 4️⃣ INVENTORY
            log.info("📦 [4/9] Seeding inventory data...");
            inventorySeeder.run();

            // 5️⃣ PO (발주)
            log.info("🧾 [5/9] Seeding purchase orders (PO_HEADER / PO_ITEM)...");
            poSeeder.run();

            // 6️⃣ GOODS RECEIPT
            log.info("🚚 [6/9] Seeding GR (Goods Receipt)...");
            goodsReceiptSeeder.run();

            // 7️⃣ LOT
            log.info("📋 [7/9] Seeding LOT records (ExpiryType-aware)...");
            lotSeeder.run();

            // 8️⃣ STOCK
            log.info("🏗️ [8/9] Seeding MM_STOCK based on LOT...");
            stockSeeder.run();

            log.info("✅ [DevDataInitializer] All seeders executed successfully.");

        } catch (Exception e) {
            log.error("❌ [DevDataInitializer] Seeding failed at runtime: {}", e.getMessage(), e);
            throw new RuntimeException("Data seeding failed: " + e.getMessage(), e);
        }
    }
}
