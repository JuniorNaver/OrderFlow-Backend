package com.youthcase.orderflow.sd.sdPayment.service;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdPayment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.dto.PaymentResult;
import com.youthcase.orderflow.sd.sdPayment.strategy.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProcessor {

    private final Map<String, PaymentStrategy> strategies;

    public PaymentResult processPayment(PaymentRequest request) {
        String methodKey = request.getPaymentMethod().getKey().toLowerCase();
        PaymentStrategy strategy = strategies.get(methodKey);

        if (strategy == null) {
            throw new IllegalArgumentException("지원하지 않는 결제 수단: " + request.getPaymentMethod());
        }

        log.info("결제 프로세스 시작: 결제수단={}, 금액={}", methodKey, request.getAmount());
        return strategy.pay(request);
    }

    public void cancelPayment(String method, PaymentItem item) {
        PaymentStrategy strategy = strategies.get(method.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("지원하지 않는 결제 수단: " + method);
        }

        if (item == null) {
            log.warn("⚠️ 결제 취소 요청 실패: PaymentItem이 null입니다.");
            return;
        }

        log.info("결제 취소 요청: 결제수단={}, 아이템ID={}", method, item.getPaymentItemId());
        strategy.cancel(item);
    }
}