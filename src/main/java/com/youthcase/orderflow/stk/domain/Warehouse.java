package com.youthcase.orderflow.stk.domain;

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
    private String warehouseId; //pk

    @Column(name = "STORAGE_CONDITION", length = 10, nullable = false)
    private String storageCondition;

    @Column(name = "MAX_CAPACITY", nullable = false)
    private Double maxCapacity; // 최대 적재 용량

    @Column(name = "CURRENT_CAPACITY", nullable = false)
    private Double currentCapacity; // 현재 적재 용량

    @Column(name = "SPOT_ID", nullable = false)
    private Long spotId; // 지점 ID (FK)

    @Builder // ⬅️ Builder를 사용하여 명시적인 생성자 추가 (DTO에서 사용할 생성자)
    public Warehouse(String warehouseId, String storageCondition, Double maxCapacity, Double currentCapacity, Long spotId) {
        this.warehouseId = warehouseId;
        this.storageCondition = storageCondition;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
        this.spotId = spotId;
    }

    /**
     * 현재 적재 용량을 증가시킵니다.
     */
    public void increaseCapacity(double amount) {
        if (this.currentCapacity + amount > this.maxCapacity) {
            throw new IllegalArgumentException("최대 용량을 초과하여 재고를 입고할 수 없습니다.");
        }
        this.currentCapacity += amount;
    }

    /**
     * 현재 적재 용량을 감소시킵니다.
     */
    public void decreaseCapacity(double amount) {
        if (this.currentCapacity - amount < 0) {
            // 이 예외는 실제로는 발생하지 않아야 하지만, 안전장치로 추가
            throw new IllegalArgumentException("현재 적재된 용량보다 많이 출고할 수 없습니다.");
        }
        this.currentCapacity -= amount;
    }
}
