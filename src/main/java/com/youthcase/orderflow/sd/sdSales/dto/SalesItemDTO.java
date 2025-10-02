package com.youthcase.orderflow.sd.sdSales.dto;

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
}
