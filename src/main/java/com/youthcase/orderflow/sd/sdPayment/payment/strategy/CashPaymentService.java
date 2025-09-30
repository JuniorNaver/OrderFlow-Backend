package com.youthcase.orderflow.sd.sdPayment.payment.strategy;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentResult;
import org.springframework.stereotype.Service;

@Service("cash")
public class CashPaymentService implements PaymentStrategy {

    @Override
    public PaymentResult pay(PaymentRequest request) {
        System.out.println("현금 결제 처리 완료");
        return new PaymentResult(true, "현금 결제 완료", null);
    }

    @Override
    public void cancel(PaymentHeader header) {
        System.out.println("현금 결제 취소 처리 완료");
    }
}