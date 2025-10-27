package com.youthcase.orderflow.master.warehouse.domain;

import com.youthcase.orderflow.common.sequence.StringIdGenerator;
import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.product.domain.StorageMethod; // ✅ 동일 enum 참조
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 📦 Warehouse (창고 마스터)
 * - 문자열 ID ("W001" 등) 자동 생성
 * - Oracle 시퀀스(Warehouse_SEQ) + StringIdGenerator 기반
 */
@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "WAREHOUSE_MASTER")
public class Warehouse {

    // ────────────────────────────────
    // 🔹 기본 키
    // ────────────────────────────────
    @Id
    @Column(name = "WAREHOUSE_ID", length = 10, nullable = false)
    private String warehouseId;

    // ────────────────────────────────
    // 🔹 창고 이름
    // ────────────────────────────────
    @Column(name = "WAREHOUSE_NAME", length = 100, nullable = false)
    @Comment("창고 이름 (예: 강남점 냉장 창고)")
    private String warehouseName;

    // ────────────────────────────────
    // 🔹 저장 방식 (실온 / 냉장 / 냉동)
    // ────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "STORAGE_METHOD", length = 20, nullable = false)
    @Comment("보관 방식 (실온/냉장/냉동)")
    private StorageMethod storageMethod; // ✅ Product 도메인 enum과 동일한 타입

    // ────────────────────────────────
    // 🔹 용량 (CBM)
    // ────────────────────────────────
    @Column(name = "CAPACITY_UOM", length = 10, nullable = false)
    private final String capacityUom = "CBM"; // 단위 고정

    @Column(name = "MAX_CAPACITY", nullable = false)
    private Double maxCapacity; // 최대 적재 부피 (CBM)

    @Column(name = "CURRENT_CAPACITY", nullable = false)
    private Double currentCapacity; // 현재 적재 부피 (CBM)

    // ────────────────────────────────
    // 🔹 지점 (FK)
    // ────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE) // ✅ Store 삭제 시 Warehouse 자동 삭제
    @Comment("소속 지점 (STORE_MASTER 참조)")
    private Store store;

    // ────────────────────────────────
    // 🔹 생성자 (Builder)
    // ────────────────────────────────
    @Builder
    public Warehouse(String warehouseName, StorageMethod storageMethod,
                     Double maxCapacity, Double currentCapacity, Store store) {
        this.warehouseName = warehouseName;
        this.storageMethod = storageMethod;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
        this.store = store;
    }

    // ────────────────────────────────
    // 🔹 ID 자동 생성 (StringIdGenerator)
    // ────────────────────────────────
    @PrePersist
    public void prePersist() {
        if (this.warehouseId == null || this.warehouseId.isBlank()) {
            this.warehouseId = WarehouseIdGenerator.generate(); // ✅ 정적 헬퍼 사용
        }
    }

    // ────────────────────────────────
    // 🔹 비즈니스 로직
    // ────────────────────────────────

    /** 창고 적재량 증가 (입고 시) */
    public void increaseCapacity(double amountCbm) {
        if (this.currentCapacity + amountCbm > this.maxCapacity) {
            throw new IllegalArgumentException("최대 용량을 초과하여 재고를 입고할 수 없습니다.");
        }
        this.currentCapacity += amountCbm;
    }

    /** 창고 적재량 감소 (출고 시) */
    public void decreaseCapacity(double amountCbm) {
        if (this.currentCapacity - amountCbm < 0) {
            throw new IllegalArgumentException("현재 적재된 용량보다 많이 출고할 수 없습니다.");
        }
        this.currentCapacity -= amountCbm;
    }

    /** 창고 정보 갱신 (이름/보관방식/용량) */
    public void updateInfo(String warehouseName, StorageMethod storageMethod, Double maxCapacity) {
        if (warehouseName != null && !warehouseName.isBlank()) this.warehouseName = warehouseName;
        if (storageMethod != null) this.storageMethod = storageMethod;
        if (maxCapacity != null && maxCapacity > 0) this.maxCapacity = maxCapacity;
    }

    // ────────────────────────────────
    // 🔹 정적 내부 컴포넌트 (Spring Context 접근용)
    // ────────────────────────────────
    @Component
    public static class WarehouseIdGenerator {

        private static StringIdGenerator stringIdGenerator;

        @Autowired
        public WarehouseIdGenerator(StringIdGenerator generator) {
            WarehouseIdGenerator.stringIdGenerator = generator;
        }

        public static String generate() {
            return stringIdGenerator.generateId("W", "WAREHOUSE_SEQ");
        }
    }
}
