package com.youthcase.orderflow.master.warehouse.dto;

import com.youthcase.orderflow.master.product.domain.StorageMethod;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WarehouseResponseDTO {

    private String warehouseId;
    private String warehouseName;
    private StorageMethod storageMethod;
    private Double maxCapacity;
    private Double currentCapacity;
    private String storeId;
    private String storeName;

    public WarehouseResponseDTO(Warehouse warehouse) {
        this.warehouseId = warehouse.getWarehouseId();
        this.warehouseName = warehouse.getWarehouseName();
        this.storageMethod = warehouse.getStorageMethod();
        this.maxCapacity = warehouse.getMaxCapacity();
        this.currentCapacity = warehouse.getCurrentCapacity();

        if (warehouse.getStore() != null) {
            this.storeId = warehouse.getStore().getStoreId();
            this.storeName = warehouse.getStore().getStoreName();
        }
    }
}
