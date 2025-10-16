package com.youthcase.orderflow.sd.sdRefund.strategy;

import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundStatus;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service("EASY")
@Slf4j
public class EasyRefundStrategy implements RefundStrategy {

    @Value("${iamport.api-key}")
    private String apiKey;

    @Value("${iamport.api-secret}")
    private String apiSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getAccessToken() {
        String url = "https://api.iamport.kr/users/getToken";
        Map<String, String> body = new HashMap<>();
        body.put("imp_key", apiKey);
        body.put("imp_secret", apiSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        Map response = restTemplate.postForObject(url, entity, Map.class);
        Map responseBody = (Map) response.get("response");

        return (String) responseBody.get("access_token");
    }

    @Override
    public boolean verify(RefundHeader header) {
        try {
            String impUid = header.getPaymentHeader()
                    .getPaymentItems()
                    .get(0)
                    .getImpUid();

            String accessToken = getAccessToken();
            String url = "https://api.iamport.kr/payments/" + impUid;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", accessToken);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            Map data = (Map) response.getBody().get("response");
            String status = (String) data.get("status");

            log.info("✅ PG 검증 성공: imp_uid={}, status={}", impUid, status);

            return "paid".equals(status);
        } catch (Exception e) {
            log.error("❌ PG 검증 중 오류", e);
            return false;
        }
    }

    @Override
    public RefundResponse refund(RefundHeader header) {
        try {
            String impUid = header.getPaymentHeader()
                    .getPaymentItems()
                    .get(0)
                    .getImpUid();

            String accessToken = getAccessToken();
            String url = "https://api.iamport.kr/payments/cancel";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("imp_uid", impUid);
            body.put("reason", header.getReason());
            body.put("amount", header.getRefundAmount()); // 부분환불 가능
            body.put("checksum", header.getRefundAmount()); // 안전성 검증용 (선택)

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map res = (Map) response.getBody().get("response");

            if (res != null && "cancelled".equals(res.get("status"))) {
                header.setRefundStatus(RefundStatus.COMPLETED);
                header.setApprovedTime(LocalDateTime.now());
                log.info("✅ 간편결제 환불 성공 imp_uid={}", impUid);
            } else {
                header.setRefundStatus(RefundStatus.FAILED);
                log.warn("⚠️ 간편결제 환불 실패 응답={}", res);
            }
        } catch (Exception e) {
            header.setRefundStatus(RefundStatus.FAILED);
            log.error("❌ 간편결제 환불 실패", e);
        }

        return RefundResponse.builder()
                .refundId(header.getRefundId())
                .paymentId(header.getPaymentHeader().getPaymentId())
                .refundAmount(header.getRefundAmount().doubleValue())
                .refundStatus(header.getRefundStatus())
                .reason(header.getReason())
                .requestedTime(header.getRequestedTime())
                .approvedTime(header.getApprovedTime())
                .build();
    }
}