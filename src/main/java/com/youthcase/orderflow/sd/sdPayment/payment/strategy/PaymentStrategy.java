package com.youthcase.orderflow.sd.sdPayment.payment.strategy;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentResult;

public interface PaymentStrategy {
    PaymentResult pay(PaymentRequest request);
    void cancel(PaymentHeader header);
}
