package com.youthcase.orderflow.sd.sdPayment.payment.strategy;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdPayment.payment.client.KakaoPayClient;
import com.youthcase.orderflow.sd.sdPayment.payment.client.TossPayClient;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("easy")
@RequiredArgsConstructor
public class EasyPaymentService implements PaymentStrategy {

    private final TossPayClient tossPayClient;
    private final KakaoPayClient kakaoPayClient;

    @Override
    public PaymentResult pay(PaymentRequest request) {
        log.info("간편결제 요청 - provider: {}", request.getProvider());

        // ✅ PaymentRequest → EasyPaymentRequest 변환
        EasyPaymentRequest easyReq = EasyPaymentRequest.builder()
                .provider(request.getProvider())
                .orderId(String.valueOf(request.getOrderId()))
                .userId("USER-001") // 필요 시 로그인 유저 ID로 대체
                .itemName("주문상품") // 필요 시 주문 상세명으로 교체
                .amount(request.getAmount().longValue())
                .successUrl("http://localhost:5173/payment/success")
                .failUrl("http://localhost:5173/payment/fail")
                .cancelUrl("http://localhost:5173/payment/cancel")
                .build();

        switch (request.getProvider().toLowerCase()) {
            case "toss" -> {
                var tossRes = tossPayClient.ready(easyReq);
                return new PaymentResult(true, "토스페이 결제 준비 완료", tossRes.getTransactionId());
            }
            case "kakao" -> {
                var kakaoRes = kakaoPayClient.ready(easyReq);
                return new PaymentResult(true, "카카오페이 결제 준비 완료", kakaoRes.getTransactionId());
            }
            default -> throw new IllegalArgumentException("지원하지 않는 결제사: " + request.getProvider());
        }
    }

    @Override
    public void cancel(PaymentItem item) {
        if (item == null) {
            log.warn("간편결제 취소 실패: PaymentItem이 null입니다.");
            return;
        }
        log.info("간편결제 취소 완료: 승인번호={}", item.getTransactionNo());
    }
}

    /*@Override
    public PaymentResult pay(PaymentRequest request) {
        log.info("간편 결제 승인 요청: {}", request);
         TODO: Toss API 연동 시 이 자리에서 WebClient 호출
        String transactionNo = "EASY-" + UUID.randomUUID();
        return new PaymentResult(true, "간편 결제 승인 완료", transactionNo);
    }

    @Override
    public PaymentResult pay(PaymentRequest request) {
        TossResponse toss = webClient.post()
                .uri("https://api.tosspayments.com/v1/payments")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TossResponse.class)
                .block();
        return new PaymentResult(true, "Toss 간편결제 승인 완료", toss.getTransactionKey());
    }

    @Override
    public void cancel(PaymentItem item) {
        if (item == null) {
            log.warn("간편 결제 취소 실패: PaymentItem이 null입니다.");
            return;
        }
        log.info("간편 결제 취소 완료: 승인번호={}", item.getTransactionNo());
    }
}*/