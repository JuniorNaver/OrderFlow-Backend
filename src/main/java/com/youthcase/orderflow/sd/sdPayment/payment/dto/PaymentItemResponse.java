package com.youthcase.orderflow.sd.sdPayment.payment.dto;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentItemResponse {
    private Long id;
    private String method;
    private BigDecimal amount;
    private String transactionNo;

    public static PaymentItemResponse from(PaymentItem item) {
        return PaymentItemResponse.builder()
                .id(item.getPaymentItemId())
                .method(item.getPaymentMethod().name()) // Enum -> String
                .amount(item.getAmount())
                .transactionNo(item.getTransactionNo())
                .build();
    }
}