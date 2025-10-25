package com.youthcase.orderflow.gr.dto;

import com.youthcase.orderflow.gr.domain.GoodsReceiptItem;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsReceiptItemDTO {
    private Long itemNo;
    private String gtin;
    private String productName;
    private Long qty; // LOT 합계
    private String expiryCalcType;
    private LocalDate mfgDate;
    private LocalDate expDateManual;
    private String note;
    private List<LotDTO> lots;

    public static GoodsReceiptItemDTO from(GoodsReceiptItem entity) {
        return GoodsReceiptItemDTO.builder()
                .itemNo(entity.getItemNo())
                .gtin(entity.getProduct() != null ? entity.getProduct().getGtin() : null)
                .productName(entity.getProduct() != null ? entity.getProduct().getProductName() : null)
                .qty(entity.getQty())
                .expiryCalcType(entity.getExpiryCalcType() != null ? entity.getExpiryCalcType().name() : null)
                .mfgDate(entity.getMfgDate())
                .expDateManual(entity.getExpDateManual())
                .lots(entity.getLots() != null
                        ? entity.getLots().stream().map(LotDTO::from).toList()
                        : List.of())
                .build();
    }
}