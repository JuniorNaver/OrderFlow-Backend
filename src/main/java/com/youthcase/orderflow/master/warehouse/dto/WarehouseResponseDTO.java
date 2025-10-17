package com.youthcase.orderflow.master.warehouse.dto;

import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WarehouseResponseDTO {

    private String warehouseId;
    private String storageCondition;
    private Double maxCapacity;
    private Double currentCapacity;
    private String storeId;
    private String storeName;

    // Entity -> DTO 변환을 위한 생성자
    public WarehouseResponseDTO(Warehouse warehouse) {
        this.warehouseId = warehouse.getWarehouseId();
        this.storageCondition = warehouse.getStorageCondition();
        this.maxCapacity = warehouse.getMaxCapacity();
        this.currentCapacity = warehouse.getCurrentCapacity();

        // ✅ Store 정보 포함
        if (warehouse.getStore() != null) {
            this.storeId = warehouse.getStore().getStoreId();
            this.storeName = warehouse.getStore().getStoreName();
        }
    }
}