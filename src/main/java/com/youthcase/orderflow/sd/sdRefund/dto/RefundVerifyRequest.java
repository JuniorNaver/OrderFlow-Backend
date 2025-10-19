package com.youthcase.orderflow.sd.sdRefund.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundVerifyRequest {
    @NotBlank
    private String paymentMethod; // CARD | CASH | EASY

    // EASY 전용
    private String impUid;

    // CARD 전용 (내부 모의결제 거래번호)
    private String transactionNo;

    // CASH 전용 (영수증 번호)
    private String receiptNo;
}