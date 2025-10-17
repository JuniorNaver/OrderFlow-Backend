package com.youthcase.orderflow.master.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "WAREHOUSE_MASTER")
public class Warehouse {

    @Id
    @Column(name = "WAREHOUSE_ID", length = 50, nullable = false)
    private String warehouseId;

    @Column(name = "STORAGE_CONDITION", length = 10, nullable = false)
    private String storageCondition;

    // 용량 단위 필드 추가 (CBM, Cubic Meter)
    // 부피를 측정하는 표준 단위임을 명시합니다.
    @Column(name = "CAPACITY_UOM", length = 10, nullable = false)
    private final String capacityUom = "CBM";

    @Column(name = "MAX_CAPACITY", nullable = false)
    private Double maxCapacity; // 최대 적재 부피 (CBM 단위)

    @Column(name = "CURRENT_CAPACITY", nullable = false)
    private Double currentCapacity; // 현재 적재 부피 (CBM 단위)

    @Column(name = "SPOT_ID", nullable = false)
    private Long spotId;

    @Builder
    public Warehouse(String warehouseId, String storageCondition, Double maxCapacity, Double currentCapacity, Long spotId) {
        this.warehouseId = warehouseId;
        this.storageCondition = storageCondition;
        // this.capacityUom은 final 필드로 선언하여 빌더에서 제외하고 "CBM"으로 고정합니다.
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
        this.spotId = spotId;
    }

    /**
     * 현재 적재 용량(CBM)을 증가시킵니다.
     */
    public void increaseCapacity(double amountCbm) {
        if (this.currentCapacity + amountCbm > this.maxCapacity) {
            throw new IllegalArgumentException("최대 용량을 초과하여 재고를 입고할 수 없습니다.");
        }
        this.currentCapacity += amountCbm;
    }

    /**
     * 현재 적재 용량(CBM)을 감소시킵니다.
     */
    public void decreaseCapacity(double amountCbm) {
        if (this.currentCapacity - amountCbm < 0) {
            throw new IllegalArgumentException("현재 적재된 용량보다 많이 출고할 수 없습니다.");
        }
        this.currentCapacity -= amountCbm;
    }
}