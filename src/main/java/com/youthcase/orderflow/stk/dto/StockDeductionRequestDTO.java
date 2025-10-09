package com.youthcase.orderflow.stk.dto; //재고 차감 요청 DTO

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockDeductionRequestDTO {

    // 재고 차감 요청을 유발한 원본 정보 (주문 상세 항목 등)
    @NotNull(message = "주문 항목 ID는 필수입니다.")
    private Long orderItemId;

    // 차감할 상품의 고유 식별자 (GTIN)
    @NotNull(message = "GTIN은 필수입니다.")
    private String gtin;

    // 차감할 수량
    @NotNull(message = "차감할 수량은 필수입니다.")
    private Integer quantityToDeduct;

    // (선택적) 특정 창고에서만 차감해야 할 경우 사용
    // private String preferredWarehouseId;
}