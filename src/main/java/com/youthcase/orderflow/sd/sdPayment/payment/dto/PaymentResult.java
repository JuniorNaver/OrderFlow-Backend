package com.youthcase.orderflow.sd.sdPayment.payment.dto;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResult {
    private boolean success;//결제 성공여부
    private String message;//응답 메세지
    private String transactionNo;//승인번호(카드/간편결제)
    private PaymentMethod method;   //어떤 결제 수단으로 처리됐는지
    private Long orderId;           //어떤 주문의 결제였는지
    private BigDecimal paidAmount;  //실제 결제된 금액
}
