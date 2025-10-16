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
        return new SalesItemDTO(
                s.getProduct().getProductName(),
                s.getSdPrice(),
                s.getSalesQuantity(),
                s.getStk() != null ? s.getStk().getQuantity() : 0
        );
    }
}
