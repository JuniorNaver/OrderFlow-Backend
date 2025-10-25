package com.youthcase.orderflow.master.price.dto;

import com.youthcase.orderflow.master.price.domain.Price;
import lombok.*;
import java.math.BigDecimal;

/**
 * 📥 PriceResponseDTO (기본형)
 * - PriceMaster의 핵심 데이터만 포함
 * - Product 정보는 제외 (성능 최적화)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceResponseDTO {

    /** 상품 GTIN (Product PK, FK) */
    private String gtin;

    /** 매입 단가 (본사 → 공급사) */
    private BigDecimal purchasePrice;

    /** 매출 단가 (본사 → 가맹점/소비자) */
    private BigDecimal salePrice;

    /**
     * 📌 Entity → DTO 변환 (기본형)
     */
    public static PriceResponseDTO from(Price entity) {
        if (entity == null) return null;
        return PriceResponseDTO.builder()
                .gtin(entity.getGtin())
                .purchasePrice(entity.getPurchasePrice())
                .salePrice(entity.getSalePrice())
                .build();
    }
}