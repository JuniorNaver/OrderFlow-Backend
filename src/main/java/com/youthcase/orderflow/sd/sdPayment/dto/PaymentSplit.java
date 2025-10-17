package com.youthcase.orderflow.sd.sdPayment.dto;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;

/**
 * 💳 PaymentSplit
 * - 결제 수단 하나의 세부 내역 (예: 카드 20,000 / 현금 10,000)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSplit {
    private PaymentMethod method;   // 결제 수단
    private BigDecimal amount;      // 결제 금액
}

