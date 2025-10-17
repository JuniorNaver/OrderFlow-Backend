package com.youthcase.orderflow.sd.sdSales.dto;

import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class SalesItemDTO {

    private final String productName;
    private final BigDecimal sdPrice;
    private final int salesQuantity;
    private final int stockQuantity;

    // ✅ 엔티티 → DTO 변환 (NPE 안전)
    public static SalesItemDTO from(SalesItem s) {
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