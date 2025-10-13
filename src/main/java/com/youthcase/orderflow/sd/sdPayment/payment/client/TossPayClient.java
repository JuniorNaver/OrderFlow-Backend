package com.youthcase.orderflow.sd.sdPayment.payment.client;

import com.youthcase.orderflow.sd.sdPayment.payment.dto.EasyPaymentApproveRequest;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.EasyPaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.EasyPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TossPayClient {

    private final RestTemplate restTemplate;
    private static final String HOST = "https://api.tosspayments.com";
    private static final String SECRET_KEY = "test_sk_xxx"; // 토스 테스트키

    public EasyPaymentResponse ready(EasyPaymentRequest req) {
        HttpHeaders headers = new HttpHeaders();

        // ✅ Toss는 BasicAuth 헤더를 직접 Base64 인코딩해야 함
        String encodedAuth = Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ✅ 요청 본문 (JSON)
        Map<String, Object> body = Map.of(
                "amount", req.getAmount(),
                "orderId", req.getOrderId(),
                "orderName", req.getItemName(),
                "successUrl", req.getSuccessUrl(),
                "failUrl", req.getFailUrl()
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // ✅ 1. 결제 준비 요청
        ResponseEntity<Map> response = restTemplate.postForEntity(
                HOST + "/v1/payments", entity, Map.class);

        Map<String, Object> resBody = response.getBody();
        if (resBody == null) {
            throw new IllegalStateException("토스페이 응답이 비어 있습니다.");
        }

        // ✅ 2. 결제 페이지 리디렉트 URL + 식별자 반환
        return EasyPaymentResponse.builder()
                .success(true)
                .message("토스페이 결제 준비 완료")
                .redirectUrl((String) resBody.get("checkoutPage"))
                .transactionId((String) resBody.get("paymentKey"))
                .build();
    }

    public EasyPaymentResponse approve(EasyPaymentApproveRequest req) {
        HttpHeaders headers = new HttpHeaders();

        // ✅ Toss 승인 요청은 동일한 인증 사용
        String encodedAuth = Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ✅ 승인 API에 필요한 데이터
        Map<String, Object> body = Map.of(
                "paymentKey", req.getTid(),     // 카카오의 TID와 유사
                "orderId", req.getOrderId(),
                "amount", req.getAmount()       // 승인할 금액 (필요 시 추가)
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // ✅ 승인 요청 (토스 API 문서 기준)
        ResponseEntity<Map> response = restTemplate.postForEntity(
                HOST + "/v1/payments/confirm", entity, Map.class);

        Map<String, Object> resBody = response.getBody();
        if (resBody == null) {
            throw new IllegalStateException("토스페이 승인 응답이 비어 있습니다.");
        }

        return EasyPaymentResponse.builder()
                .success(true)
                .message("토스페이 결제 승인 완료")
                .transactionId((String) resBody.get("paymentKey"))
                .approved(true)
                .build();
    }
}
