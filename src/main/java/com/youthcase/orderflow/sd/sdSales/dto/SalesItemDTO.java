package com.youthcase.orderflow.sd.sdSales.dto;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class SalesItemDTO {

    private final Long id;             // SalesItem.no
    private final String gtin;         // ✅ 추가: Product.GTIN (프론트 중복검출용)
    private final String productName;
    private final BigDecimal sdPrice;
    private final int salesQuantity;
    private final int stockQuantity;
    private final BigDecimal subtotal; // 계산용 필드

    public static SalesItemDTO from(SalesItem s) {
        String name = (s.getProduct() != null && s.getProduct().getProductName() != null)
                ? s.getProduct().getProductName()
                : "상품명 미등록";

        String gtin = (s.getProduct() != null && s.getProduct().getGtin() != null)
                ? s.getProduct().getGtin()
                : "UNKNOWN";

        BigDecimal price = s.getSdPrice() != null ? s.getSdPrice() : BigDecimal.ZERO;
        int stock = (s.getStk() != null && s.getStk().getQuantity() != null)
                ? s.getStk().getQuantity()
                : 0;

        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(s.getSalesQuantity()));

        return new SalesItemDTO(
                s.getNo(),
                gtin, // ✅ GTIN 포함
                name,
                price,
                s.getSalesQuantity(),
                stock,
                subtotal
        );
    }
}