package com.youthcase.orderflow.master.warehouse.dto;

import com.youthcase.orderflow.master.product.domain.StorageMethod;
import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 🏗️ Warehouse 등록/수정용 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class WarehouseRequestDTO {

    /**
     * 🔹 수정용으로만 사용 (등록 시에는 자동 생성됨)
     */
    private String warehouseId; // Optional (등록 시 null 가능)

    @NotBlank(message = "창고 이름은 필수입니다.")
    private String warehouseName;

    @NotNull(message = "보관 방식은 필수입니다.")
    private StorageMethod storageMethod; // Enum: 실온, 냉장, 냉동

    @NotNull(message = "최대 적재 용량은 필수입니다.")
    private Double maxCapacity;

    @NotNull(message = "지점 ID는 필수입니다.")
    private String storeId;

    // ────────────────────────────────
    // DTO → Entity 변환 (등록용)
    // ────────────────────────────────
    public Warehouse toEntity(Store store) {
        // ID는 @PrePersist에서 자동 생성됨
        return Warehouse.builder()
                .warehouseName(this.warehouseName)
                .storageMethod(this.storageMethod)
                .maxCapacity(this.maxCapacity)
                .currentCapacity(0.0)
                .store(store)
                .build();
    }

    // ────────────────────────────────
    // 기존 Entity에 덮어쓰기 (수정용)
    // ────────────────────────────────
    public void applyToEntity(Warehouse warehouse, Store store) {
        if (warehouseName != null && !warehouseName.isBlank()) warehouse.setWarehouseName(warehouseName);
        if (storageMethod != null) warehouse.setStorageMethod(storageMethod);
        if (maxCapacity != null && maxCapacity > 0) warehouse.setMaxCapacity(maxCapacity);
        if (store != null) warehouse.setStore(store);
    }
}
