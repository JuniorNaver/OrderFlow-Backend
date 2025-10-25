package com.youthcase.orderflow.master.price.dto;

import com.youthcase.orderflow.master.price.domain.Price;
import com.youthcase.orderflow.master.product.domain.Product;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * 📤 PriceRequestDTO
 * - 매입/매출 단가 등록 및 수정 요청용 DTO
 * - GTIN은 Product와 1:1로 연결되므로 필수
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceRequestDTO {

    /** 상품 GTIN (Product PK) */
    @NotBlank(message = "GTIN은 필수 값입니다.")
    private String gtin;

    /** 매입 단가 (본사 → 공급사) */
    @NotNull(message = "매입 단가는 필수 값입니다.")
    @DecimalMin(value = "0.00", message = "매입 단가는 0 이상이어야 합니다.")
    @Digits(integer = 10, fraction = 2, message = "매입 단가는 최대 10자리 정수와 2자리 소수까지 가능합니다.")
    private BigDecimal purchasePrice;

    /** 매출 단가 (본사 → 가맹점/소비자) */
    @NotNull(message = "매출 단가는 필수 값입니다.")
    @DecimalMin(value = "0.00", message = "매출 단가는 0 이상이어야 합니다.")
    @Digits(integer = 10, fraction = 2, message = "매출 단가는 최대 10자리 정수와 2자리 소수까지 가능합니다.")
    private BigDecimal salePrice;

    /**
     * 📌 DTO → Entity 변환
     * - Product 객체를 인자로 받아 Price 엔티티를 생성
     */
    public Price toEntity(Product product) {
        return Price.builder()
                .product(product)
                .gtin(product.getGtin())
                .purchasePrice(this.purchasePrice)
                .salePrice(this.salePrice)
                .build();
    }
}
