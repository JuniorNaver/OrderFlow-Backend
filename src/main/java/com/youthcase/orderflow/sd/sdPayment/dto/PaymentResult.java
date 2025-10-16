package com.youthcase.orderflow.sd.sdPayment.dto;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResult {
    private boolean success;           // 결제 성공 여부
    private String message;            // 응답 메시지

    private String impUid;             // 아임포트 결제 고유 ID (EASY 전용)
    private String transactionNo;      // 거래 승인번호 또는 impUid

    private PaymentMethod method;      // 결제 수단 (CARD, CASH, EASY)
    private Long orderId;              // 주문 ID
    private BigDecimal paidAmount;     // 실제 결제 금액
}


