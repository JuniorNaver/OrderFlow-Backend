package com.youthcase.orderflow.gr.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class GoodsReceiptItemDTO {
    private Long itemNo;
    private String gtin;
    private Integer qty;
    private String note;
}
