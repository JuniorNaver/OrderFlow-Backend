package com.youthcase.orderflow.mockTest.init;

import com.youthcase.orderflow.mockTest.*;
import com.youthcase.orderflow.mockTest.auth.*;
import com.youthcase.orderflow.mockTest.gr.*;
import com.youthcase.orderflow.mockTest.master.*;
import com.youthcase.orderflow.mockTest.pr.*;
import com.youthcase.orderflow.mockTest.sd.*;
import com.youthcase.orderflow.mockTest.stk.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 🚀 DevDataInitializer
 * --------------------------------------------------------
 * - dev/local 환경 전체 데이터 시더 실행기
 * - FK 의존 순서 기반 실행 (Cleaner → Auth → Master → User → Inventory → Stock)
 * - GR/LOT/STK 시더 분리로 실제 물류 흐름 반영
 * --------------------------------------------------------
 */
@Slf4j
@Component
@Profile({"dev", "local"})
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

    // ====== INVENTORY / GR / LOT / STOCK ======
    private final InventorySeeder inventorySeeder;
    private final GoodsReceiptSeeder goodsReceiptSeeder;
    private final LotSeeder lotSeeder;
    private final StockSeeder stockSeeder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🚀 [DevDataInitializer] Starting full mock data initialization...");

        try {
            // 0️⃣ 초기화 전 데이터 정리 (선택)
            log.info("🧹 Cleaning existing data before seeding...");
            // DevDataCleaner가 별도로 Order(0)에서 실행되므로 중복 호출은 생략 가능

            // 1️⃣ AUTH & ROLE
            log.info("🔐 [1/6] Seeding authorities and roles...");
            authoritySeeder.run();
            roleSeeder.run();
            roleAuthoritySeeder.run();

            // 2️⃣ MASTER (Store / Warehouse / Category / Product / Price)
            log.info("🏪 [2/6] Seeding master data (Store, Warehouse, Product, Price)...");
            storeSeeder.run();
            warehouseSeeder.run();
            categorySeeder.run();
            productSeeder.run();
            priceSeeder.run();

            // 3️⃣ USER
            log.info("👤 [3/6] Seeding user accounts...");
            appUserSeeder.run();

            // 4️⃣ INVENTORY
            log.info("📦 [4/6] Seeding inventory data...");
            inventorySeeder.run();

            // 5️⃣ 입고 → LOT → 재고
            log.info("🚚 [5/6] Seeding GR (Goods Receipt)...");
            goodsReceiptSeeder.run();

            log.info("📋 [5/6-2] Seeding LOT records...");
            lotSeeder.run();

            log.info("🏗️ [5/6-3] Seeding MM_STOCK based on LOT...");
            stockSeeder.run();

            log.info("✅ [DevDataInitializer] All seeders executed successfully.");

        } catch (Exception e) {
            log.error("❌ [DevDataInitializer] Seeding failed: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
