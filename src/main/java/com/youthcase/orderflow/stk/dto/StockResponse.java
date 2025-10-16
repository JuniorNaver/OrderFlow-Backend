package com.youthcase.orderflow.stk.dto;

import com.youthcase.orderflow.stk.domain.STK;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockResponse {
    private String gtin;            // ✅ 프론트용 추가
    private String produceName;
    private BigDecimal price;       // ✅ 단가 표시용
    private Integer quantity;
    private String status;

    public static StockResponse fromEntity(STK s) {
        return StockResponse.builder()
                .gtin(s.getProduct().getGtin())          // ✅ 핵심: GTIN 주입
                .produceName(s.getProduct().getProductName())
                .price(s.getProduct().getPrice())        // ✅ 상품 가격
                .quantity(s.getQuantity())
                .status(s.getStatus())
                .build();
    }
}
