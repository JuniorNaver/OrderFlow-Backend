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
    private String lotNo;
    private Long qty;
    private LocalDate mfgDate;
    private LocalDate expDate;
    private String status;

    public static LotDTO from(Lot entity) {
        return LotDTO.builder()
                .lotId(entity.getLotId())
                .gtin(entity.getProduct() != null ? entity.getProduct().getGtin() : null)
                .lotNo(entity.getLotNo())
                .qty(entity.getQty())
                .mfgDate(entity.getMfgDate())
                .expDate(entity.getExpDate())
                .status(entity.getStatus().name())
                .build();
    }
}