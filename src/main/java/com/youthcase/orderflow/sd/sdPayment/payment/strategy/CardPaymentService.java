package com.youthcase.orderflow.sd.sdPayment.payment.strategy;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentResult;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("card")
public class CardPaymentService implements PaymentStrategy {

    @Override
    public PaymentResult pay(PaymentRequest request) {
        // TODO: RestTemplate/WebClient로 PG API 호출
        System.out.println("PG사 카드 결제 요청: " + request);
        return new PaymentResult(true, "카드 결제 승인 완료", "CARD-" + UUID.randomUUID());
    }

    @Override
    public void cancel(PaymentHeader header) {
        System.out.println("PG사 카드 결제 취소: " + header.getTransactionNo());
    }
}
