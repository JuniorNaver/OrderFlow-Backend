package com.youthcase.orderflow.sd.sdPayment.payment.strategy;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service("card")
public class CardPaymentService implements PaymentStrategy {

    @Override
    public PaymentResult pay(PaymentRequest request) {
        // TODO: RestTemplate/WebClient로 PG API 호출
        log.info("PG 카드 결제 요청: {}", request);
        String transactionNo = "CARD-" + UUID.randomUUID();
        return new PaymentResult(true, "카드 결제 승인 완료", transactionNo);
    }

    @Override
    public void cancel(PaymentItem item) {
        if (item == null) {
            log.warn("카드 결제 취소 실패: PaymentItem이 null입니다.");
            return;
        }
        log.info("카드 결제 취소 처리 완료: 승인번호={}", item.getTransactionNo());
    }
}
