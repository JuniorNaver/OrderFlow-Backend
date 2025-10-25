package com.youthcase.orderflow.po.dto;

import com.youthcase.orderflow.master.price.domain.Price;
import com.youthcase.orderflow.po.domain.POHeader;
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
    private LocalDate expectedArrival;
    private Price purchasePrice;
    private String productName;
    private Long orderQty;
    private Long pendingQty;
    private Long shippedQty;
    private Long total;
    private POHeader poHeader;
    private String gtin;
    private POStatus status;
}