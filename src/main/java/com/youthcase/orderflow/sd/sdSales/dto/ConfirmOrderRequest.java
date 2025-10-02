package com.youthcase.orderflow.sd.sdSales.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmOrderRequest {
    private Long orderId;
    private List<ItemDTO> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDTO {
        private String gtin;
        private int quantity;
        private BigDecimal price;
    }
}