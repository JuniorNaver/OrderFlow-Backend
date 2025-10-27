package com.youthcase.orderflow.mockTest.master;

import com.youthcase.orderflow.master.product.domain.StorageMethod;
import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.store.repository.StoreRepository;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import com.youthcase.orderflow.master.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 🏗️ WarehouseSeeder (무조건 생성형)
 * --------------------------------------------------------
 * - Store S001 참조, 항상 새로운 창고 데이터 생성
 * - WAREHOUSE_SEQ 기반 자동 ID 생성
 * --------------------------------------------------------
 */
@Slf4j
@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class WarehouseSeeder {

    private final WarehouseRepository warehouseRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public void run(String... args) {
        log.info("🏗️ [WarehouseSeeder] Creating default warehouses (forced insert)...");

        Store store = storeRepository.findById("S001")
                .orElseThrow(() -> new IllegalStateException("⚠️ Store S001 not found — run StoreSeeder first."));

        Warehouse wh1 = Warehouse.builder()
                .warehouseName("실온 창고")
                .storageMethod(StorageMethod.ROOM_TEMP)
                .maxCapacity(500.0)
                .currentCapacity(0.0)
                .store(store)
                .build();

        Warehouse wh2 = Warehouse.builder()
                .warehouseName("냉장 창고")
                .storageMethod(StorageMethod.COLD)
                .maxCapacity(300.0)
                .currentCapacity(0.0)
                .store(store)
                .build();

        Warehouse wh3 = Warehouse.builder()
                .warehouseName("냉동 창고")
                .storageMethod(StorageMethod.FROZEN)
                .maxCapacity(200.0)
                .currentCapacity(0.0)
                .store(store)
                .build();

        warehouseRepository.saveAndFlush(wh1);
        warehouseRepository.saveAndFlush(wh2);
        warehouseRepository.saveAndFlush(wh3);

        log.info("✅ [WarehouseSeeder] Warehouses created successfully for store {}", store.getStoreId());
    }
}
