package com.youthcase.orderflow.sd.sdReceipt.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ReceiptItemDTO {
    private Long receiptItemId;
    private Long salesItemId;
    private Long paymentItemId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
