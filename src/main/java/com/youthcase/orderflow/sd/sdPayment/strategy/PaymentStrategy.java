package com.youthcase.orderflow.sd.sdPayment.strategy;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdPayment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.dto.PaymentResult;

public interface PaymentStrategy {
    PaymentResult pay(PaymentRequest request);
    void cancel(PaymentItem item);
}
