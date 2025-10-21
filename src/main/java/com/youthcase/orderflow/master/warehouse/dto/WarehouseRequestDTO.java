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

    @NotBlank(message = "창고 ID는 필수입니다.")
    private String warehouseId; // 수정 시 PathVariable로 대체 가능 (선택적으로 사용)

    @NotBlank(message = "창고 이름은 필수입니다.")
    private String warehouseName;

    @NotNull(message = "보관 방식은 필수입니다.")
    private StorageMethod storageMethod; // Enum: 실온, 냉장, 냉동

    @NotNull(message = "최대 적재 용량은 필수입니다.")
    private Double maxCapacity;

    @NotNull(message = "지점 ID는 필수입니다.")
    private String storeId;

    // ────────────────────────────────
    // DTO → Entity 변환
    // ────────────────────────────────
    public Warehouse toEntity(Store store) {
        return Warehouse.builder()
                .warehouseId(this.warehouseId)
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
