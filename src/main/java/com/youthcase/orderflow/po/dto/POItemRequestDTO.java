package com.youthcase.orderflow.po.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class POItemRequestDTO {
    private Long itemNo;
    private Long orderQty;
    private Long unitPrice;
}