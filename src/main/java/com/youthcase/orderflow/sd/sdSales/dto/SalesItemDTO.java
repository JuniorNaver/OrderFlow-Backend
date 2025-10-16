package com.youthcase.orderflow.sd.sdSales.dto;

import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class SalesItemDTO {

    private String productName;
    private BigDecimal sdPrice;
    private int salesQuantity;
    private int stockQuantity;

    public static SalesItemDTO fromEntity(SalesItem s) {
        String name = (s.getProduct() != null && s.getProduct().getProductName() != null)
                ? s.getProduct().getProductName()
                : "상품명 미등록";

        BigDecimal price = s.getSdPrice() != null ? s.getSdPrice() : BigDecimal.ZERO;

        int stock = 0;
        if (s.getStk() != null && s.getStk().getQuantity() != null) {
            stock = s.getStk().getQuantity();
        }

        return new SalesItemDTO(
                name,
                price,
                s.getSalesQuantity(),
                stock
        );
    }
}