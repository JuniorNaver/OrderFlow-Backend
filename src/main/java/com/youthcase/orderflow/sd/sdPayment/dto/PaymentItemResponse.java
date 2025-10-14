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
    private BigDecimal amount;      // 해당 수단 결제 금액
    private String transactionId;   // 거래 승인번호(카드/간편결제 impUid 등)

    public static PaymentItemResponse from(PaymentItem item) {
        return PaymentItemResponse.builder()
                .id(item.getPaymentItemId())
                .method(item.getPaymentMethod().name()) // Enum → String
                .amount(item.getAmount())
                .transactionId(item.getTransactionNo()) // 통일된 명칭
                .build();
    }
}