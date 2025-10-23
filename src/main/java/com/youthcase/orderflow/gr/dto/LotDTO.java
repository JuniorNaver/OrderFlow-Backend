package com.youthcase.orderflow.gr.dto;

import com.youthcase.orderflow.gr.domain.Lot;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LotDTO {
    private Long lotId;
    private String gtin;
    private String productName;
    private LocalDate expDate;
    private Long qty;

    public static LotDTO from(Lot lot) {
        return LotDTO.builder()
                .lotId(lot.getLotId())
                .gtin(lot.getProduct() != null ? lot.getProduct().getGtin() : null)
                .productName(lot.getProduct() != null ? lot.getProduct().getProductName() : null)
                .expDate(lot.getExpDate())
                .qty(lot.getQty())
                .build();
    }
}