package com.youthcase.orderflow.sd.sdSales.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class AddItemRequest {
    private Long orderId;
    private String gtin;       // 상품 바코드
    private int quantity;
    private BigDecimal price;
}
