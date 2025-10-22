package com.youthcase.orderflow.gr.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class GoodsReceiptItemDTO {
    private Long itemNo;
    private String gtin;
    private Long qty;
    private String note;
}
