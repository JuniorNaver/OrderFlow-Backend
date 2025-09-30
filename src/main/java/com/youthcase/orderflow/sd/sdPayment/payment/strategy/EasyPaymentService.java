package com.youthcase.orderflow.sd.sdPayment.payment.strategy;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentResult;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("easy")
public class EasyPaymentService implements PaymentStrategy {

    @Override
    public PaymentResult pay(PaymentRequest request) {
        System.out.println("간편 결제 승인 요청: " + request);
        return new PaymentResult(true, "간편 결제 승인 완료", "EASY-" + UUID.randomUUID());
    }

    @Override
    public void cancel(PaymentHeader header) {
        System.out.println("간편 결제 취소: " + header.getTransactionNo());
    }
}