package com.youthcase.orderflow.sd.sdPayment.dto;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import lombok.*;

import java.math.BigDecimal;

/**
 * 💳 개별 결제 항목 응답 DTO
 * - 한 주문에서 여러 결제 수단(현금+카드 등)이 사용될 수 있음
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentItemResponse {

    private Long id;                // 결제 항목 PK
    private String method;          // 결제 방식 (CARD, CASH, EASY)
    private BigDecimal amount;      // 결제 금액
    private String impUid;          // 아임포트 결제 고유번호 (EASY 전용)
    private String transactionNo;   // 승인번호 (CARD, CASH, EASY 공통)

    public static PaymentItemResponse from(PaymentItem item) {
        return PaymentItemResponse.builder()
                .id(item.getPaymentItemId())
                .method(item.getPaymentMethod().name())  // Enum → String
                .amount(item.getAmount())
                .impUid(item.getImpUid())                // EASY 결제 시 존재
                .transactionNo(item.getTransactionNo())  // 모든 결제 방식 공통
                .build();
    }
}