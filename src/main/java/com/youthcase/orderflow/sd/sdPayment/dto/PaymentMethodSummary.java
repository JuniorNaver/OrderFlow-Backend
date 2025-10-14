package com.youthcase.orderflow.sd.sdPayment.dto;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@ToString
public class PaymentMethodSummary {
    private PaymentMethod method;
    private BigDecimal totalAmount;
}
