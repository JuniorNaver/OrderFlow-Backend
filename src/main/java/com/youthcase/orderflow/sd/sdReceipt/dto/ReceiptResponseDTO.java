package com.youthcase.orderflow.sd.sdReceipt.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ReceiptResponseDTO {

    private Long receiptId;
    private String receiptNo;

    private Long paymentId;
    private Long salesId;   // null 가능
    private Long refundId;  // null 가능

    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

    private List<ReceiptItemDTO> items;
}
