package com.youthcase.orderflow.stk.dto;

import com.youthcase.orderflow.stk.domain.Warehouse;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WarehouseResponseDTO {

    private String warehouseId;
    private String storageCondition;
    private Double maxCapacity;
    private Double currentCapacity;
    private Long spotId;

    // Entity -> DTO 변환을 위한 생성자
    public WarehouseResponseDTO(Warehouse warehouse) {
        this.warehouseId = warehouse.getWarehouseId();
        this.storageCondition = warehouse.getStorageCondition();
        this.maxCapacity = warehouse.getMaxCapacity();
        this.currentCapacity = warehouse.getCurrentCapacity();
        this.spotId = warehouse.getSpotId();
    }
}