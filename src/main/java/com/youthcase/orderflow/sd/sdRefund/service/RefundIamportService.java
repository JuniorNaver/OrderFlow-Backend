package com.youthcase.orderflow.sd.sdRefund.service;

import com.youthcase.orderflow.sd.sdRefund.dto.VerifyRefundResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundIamportService {

    @Value("${iamport.api-key}")
    private String apiKey;

    @Value("${iamport.api-secret}")
    private String apiSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ 1. AccessToken 발급
    public String getAccessToken() {
        String url = "https://api.iamport.kr/users/getToken";
        Map<String, String> body = new HashMap<>();
        body.put("imp_key", apiKey);
        body.put("imp_secret", apiSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        Map response = restTemplate.postForObject(url, entity, Map.class);

        Map responseBody = (Map) response.get("response");
        String token = (String) responseBody.get("access_token");

        log.info("🔑 Iamport AccessToken 발급 완료: {}", token);
        return token;
    }

    // ✅ 3. 환불 요청
    public boolean cancelPayment(String impUid, String reason, double amount) {
        if (impUid == null || impUid.isBlank()) {
            log.error("❌ cancelPayment 실패: impUid가 비어 있습니다.");
            return false;
        }

        String token = getAccessToken();
        String url = "https://api.iamport.kr/payments/cancel";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("imp_uid", impUid);
        body.put("reason", reason);
        if (amount > 0) body.put("amount", amount);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        Map bodyMap = response.getBody();
        if (bodyMap == null || bodyMap.get("response") == null) {
            log.warn("⚠️ PG 환불 실패: {}", bodyMap);
            return false;
        }

        Map responseBody = (Map) bodyMap.get("response");
        String status = (String) responseBody.get("status");

        if ("cancelled".equals(status) || "partial_cancelled".equals(status)) {
            log.info("✅ PG 환불 성공: imp_uid={}, status={}", impUid, status);
            return true;
        } else {
            log.warn("⚠️ PG 환불 실패: {}", responseBody);
            return false;
        }
    }


    public VerifyRefundResponse verifyPayment(String impUid) {
        String token = getAccessToken();
        String url = "https://api.iamport.kr/payments/" + impUid;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map responseBody = (Map) response.getBody().get("response");

        return VerifyRefundResponse.builder()
                .impUid((String) responseBody.get("imp_uid"))
                .merchantUid((String) responseBody.get("merchant_uid"))
                .status((String) responseBody.get("status"))
                .cancelAmount(responseBody.get("amount") != null ? ((Number) responseBody.get("amount")).doubleValue() : 0.0)
                .pgProvider((String) responseBody.get("pg_provider"))
                .cancelReason((String) responseBody.get("cancel_reason"))
                .receiptUrl((String) responseBody.get("receipt_url"))
                .paidAt(ZonedDateTime.ofInstant(
                        Instant.ofEpochSecond(((Number) responseBody.get("paid_at")).longValue()),
                        ZoneId.systemDefault()
                ))
                .cancelledAt(responseBody.get("cancelled_at") != null ?
                        ZonedDateTime.ofInstant(
                                Instant.ofEpochSecond(((Number) responseBody.get("cancelled_at")).longValue()),
                                ZoneId.systemDefault()
                        ) : null)
                .build();
    }
}