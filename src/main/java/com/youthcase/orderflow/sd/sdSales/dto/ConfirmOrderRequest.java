package com.youthcase.orderflow.sd.sdSales.dto;

import com.youthcase.orderflow.master.price.domain.Price;
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
    private BigDecimal totalAmount;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDTO {
        private String gtin;
        private Long quantity;
        private BigDecimal unitPrice;
    }
}