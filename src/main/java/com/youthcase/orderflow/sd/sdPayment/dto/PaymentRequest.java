package com.youthcase.orderflow.sd.sdPayment.dto;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentMethod;
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
    private PaymentMethod paymentMethod; //결제방식

    private String impUid;             // 아임포트 결제 고유 ID (imp_1234567890)
    private String merchantUid;        // 가맹점 주문번호 (order_2025_0001 등)

}
