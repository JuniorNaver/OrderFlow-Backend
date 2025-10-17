package com.youthcase.orderflow.master.dto;

import com.youthcase.orderflow.master.domain.Warehouse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class WarehouseRequestDTO {

    @NotBlank(message = "창고 ID는 필수입니다.")
    private String warehouseId; // 등록 시 ID를 직접 입력받음

    @NotBlank(message = "보관 상태는 필수입니다.")
    private String storageCondition; // 냉동/냉장/실온

    @NotNull(message = "최대 적재 용량은 필수입니다.")
    private Double maxCapacity;

    @NotNull(message = "지점 ID는 필수입니다.")
    private Long spotId;

    // CURRENT_CAPACITY는 등록 시점에는 0 또는 널이므로 요청 DTO에서는 제외합니다.

    // DTO -> Entity 변환 메서드
    public Warehouse toEntity() {
        return Warehouse.builder()
                .warehouseId(this.warehouseId)
                .storageCondition(this.storageCondition)
                .maxCapacity(this.maxCapacity)
                .currentCapacity(0.0) // 초기 적재 용량은 0으로 설정
                .spotId(this.spotId)
                .build();
    }
}