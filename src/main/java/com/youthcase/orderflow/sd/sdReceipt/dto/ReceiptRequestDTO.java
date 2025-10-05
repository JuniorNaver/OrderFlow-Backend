package com.youthcase.orderflow.sd.sdReceipt.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptRequestDTO {
    private Long paymentId;    // 결제 기준
    private String storeName;
}