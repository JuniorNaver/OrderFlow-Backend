package com.youthcase.orderflow.stk.dto;

import com.youthcase.orderflow.stk.domain.STK;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockResponse {
    private String productName;
    private Integer quantity;
    private String status;

    public static StockResponse fromEntity(STK s) {
        return StockResponse.builder()
                .productName(
                        s.getProduct() != null ? s.getProduct().getProductName() : "상품없음"
                )
                .quantity(s.getQuantity())
                .status(s.getStatus())
                .build();
    }
}