package com.youthcase.orderflow.po.dto;

import com.youthcase.orderflow.master.price.domain.Price;
import com.youthcase.orderflow.po.domain.POStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class POItemResponseDTO {
    private Long itemNo;
    private String productName;
    private String gtin;
    private LocalDate expectedArrival;
    private Price purchasePrice;
    private Long orderQty;
    private Long total;
    private POStatus status;
}