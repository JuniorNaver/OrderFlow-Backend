package com.youthcase.orderflow.gr.dto;

import com.youthcase.orderflow.gr.status.GoodsReceiptStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GRListDTO {
    private Long poId;
    private String externalId;
    private BigDecimal totalAmount;
    private String userName;
    private GoodsReceiptStatus status;
    private LocalDate receiptDate;
}