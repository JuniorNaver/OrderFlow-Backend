package com.youthcase.orderflow.sd.sdPayment.payment;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentResult;
import com.youthcase.orderflow.sd.sdPayment.payment.strategy.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentProcessor {

    private final Map<String, PaymentStrategy> strategies;

    public PaymentResult processPayment(PaymentRequest request) {
        PaymentStrategy strategy = strategies.get(request.getPaymentMethod().toLowerCase());
        if (strategy == null) throw new IllegalArgumentException("지원하지 않는 결제 수단");
        return strategy.pay(request);
    }

    public void cancelPayment(String method, PaymentItem item) {
        PaymentStrategy strategy = strategies.get(method);
        if (strategy != null) {
            strategy.cancel(item);
        }
    }
}