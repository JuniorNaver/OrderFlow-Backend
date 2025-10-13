package com.youthcase.orderflow.sd.sdPayment.payment.client;

import com.youthcase.orderflow.sd.sdPayment.payment.dto.EasyPaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.EasyPaymentResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class KakaoPayClient {

    private final RestTemplate restTemplate;

    private static final String HOST = "https://kapi.kakao.com";
    private static final String ADMIN_KEY = "kakaoAK PRD691071ACDE5EA19D272EAB5638A866DC81670";

    public EasyPaymentResponse ready(EasyPaymentRequest req) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", ADMIN_KEY);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("partner_order_id", req.getOrderId());
        params.add("partner_user_id", req.getUserId());
        params.add("item_name", req.getItemName());
        params.add("quantity", "1");
        params.add("total_amount", String.valueOf(req.getAmount()));
        params.add("tax_free_amount", "0");
        params.add("approval_url", req.getSuccessUrl());
        params.add("cancel_url", req.getCancelUrl());
        params.add("fail_url", req.getFailUrl());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        // ✅ 1. API 호출
        ResponseEntity<Map> response = restTemplate.postForEntity(
                HOST + "/v1/payment/ready", entity, Map.class);

        // ✅ 2. 응답 Body 추출
        Map<String, Object> body = response.getBody();
        if (body == null) {
            throw new IllegalStateException("카카오페이 응답이 비어 있습니다.");
        }

        // ✅ 3. DTO 변환
        return EasyPaymentResponse.builder()
                .success(true)
                .message("카카오페이 결제 준비 완료")
                .redirectUrl((String) body.get("next_redirect_pc_url"))
                .transactionId((String) body.get("tid"))
                .build();
    }
}