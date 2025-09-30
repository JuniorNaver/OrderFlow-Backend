package com.youthcase.orderflow.sd.sdPayment.payment.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {
    private Long orderId; //주문번호
    private BigDecimal amount; //결제금액
    private String paymentMethod; //결제방식
}
