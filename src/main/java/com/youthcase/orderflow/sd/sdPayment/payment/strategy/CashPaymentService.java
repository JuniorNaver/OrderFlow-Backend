package com.youthcase.orderflow.sd.sdPayment.payment.strategy;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("cash")
public class CashPaymentService implements PaymentStrategy {

    @Override
    public PaymentResult pay(PaymentRequest request) {
        log.info("현금 결제 처리 시작: {}", request);
        return new PaymentResult(true, "현금 결제 완료", null);
    }

    @Override
    public void cancel(PaymentItem item) {
        if (item == null) {
            log.warn("현금 결제 취소 실패: PaymentItem이 null입니다.");
            return;
        }
        log.info("현금 결제 취소 완료: 결제아이템ID={}", item.getPaymentItemId());
    }
}