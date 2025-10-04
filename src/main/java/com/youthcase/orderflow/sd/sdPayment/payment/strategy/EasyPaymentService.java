package com.youthcase.orderflow.sd.sdPayment.payment.strategy;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service("easy")
public class EasyPaymentService implements PaymentStrategy {

    @Override
    public PaymentResult pay(PaymentRequest request) {
        log.info("간편 결제 승인 요청: {}", request);
        // TODO: Toss API 연동 시 이 자리에서 WebClient 호출
        String transactionNo = "EASY-" + UUID.randomUUID();
        return new PaymentResult(true, "간편 결제 승인 완료", transactionNo);
    }

    /*@Override
    public PaymentResult pay(PaymentRequest request) {
        TossResponse toss = webClient.post()
                .uri("https://api.tosspayments.com/v1/payments")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TossResponse.class)
                .block();
        return new PaymentResult(true, "Toss 간편결제 승인 완료", toss.getTransactionKey());
    }*/

    @Override
    public void cancel(PaymentItem item) {
        if (item == null) {
            log.warn("간편 결제 취소 실패: PaymentItem이 null입니다.");
            return;
        }
        log.info("간편 결제 취소 완료: 승인번호={}", item.getTransactionNo());
    }
}