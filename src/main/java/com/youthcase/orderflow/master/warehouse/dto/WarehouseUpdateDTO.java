package com.youthcase.orderflow.master.warehouse.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class WarehouseUpdateDTO {

    @NotBlank(message = "보관 상태는 필수입니다.")
    private String storageCondition;

    @NotNull(message = "최대 적재 용량은 필수입니다.")
    private Double maxCapacity;

    @NotBlank(message = "지점 ID는 필수입니다.")
    private String storeId;

    // 현재 적재 용량(CURRENT_CAPACITY)는 비즈니스 로직에 의해 변경되어야 하므로,
    // 마스터 정보 수정 API에서는 일반적으로 요청받지 않습니다.
}