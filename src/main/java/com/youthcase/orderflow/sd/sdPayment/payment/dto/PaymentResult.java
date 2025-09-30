package com.youthcase.orderflow.sd.sdPayment.payment.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResult {
    private boolean success;//결제 성공여부
    private String message;//응답 메세지
    private String transactionNo;//승인번호(카드/간편결제)
}
