package com.youthcase.orderflow.stk.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "WAREHOUSE_MASTER")
public class Warehouse {

    @Id
    @Column(name = "WAREHOUSE_ID", length = 50, nullable = false)
    private String warehouseId; //pk

    @Column(name = "STORAGE_CONDITION", length = 10, nullable = false)
    private String storageCondition;

    @Column(name = "MAX_CAPACITY", nullable = false)
    private Double maxCapacity; // 최대 적재 용량

    @Column(name = "CURRENT_CAPACITY", nullable = false)
    private Double currentCapacity; // 현재 적재 용량

    @Column(name = "SPOT_ID", nullable = false)
    private Long spotId; // 지점 ID (FK)
}
