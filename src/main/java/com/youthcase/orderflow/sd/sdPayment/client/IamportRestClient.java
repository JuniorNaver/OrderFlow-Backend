package com.youthcase.orderflow.sd.sdPayment.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
public class IamportRestClient {

    private final WebClient webClient;

    @Value("${iamport.api-key}")
    private String apiKey;

    @Value("${iamport.api-secret}")
    private String apiSecret;

    public IamportRestClient() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.iamport.kr")
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /** ✅ Access Token 발급 */
    public String getAccessToken() {
        Map<String, String> body = Map.of(
                "imp_key", apiKey,
                "imp_secret", apiSecret
        );

        JsonNode response = webClient.post()
                .uri("/users/getToken")
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), clientResponse -> {
                    log.error("❌ 아임포트 토큰 요청 실패: HTTP {}", clientResponse.statusCode());
                    return clientResponse.createException();
                })
                .bodyToMono(JsonNode.class)
                .block();

        if (response == null || response.get("response") == null) {
            throw new IllegalStateException("아임포트 토큰 응답이 비어있습니다.");
        }

        String token = response.get("response").get("access_token").asText();
        log.info("✅ [아임포트] AccessToken 발급 완료: {}", token);
        return token;
    }
    /** ✅ 결제 내역 조회 */
    public JsonNode getPaymentByImpUid(String impUid) {
        String token = getAccessToken();

        JsonNode result = webClient.get()
                .uri("/payments/{impUid}", impUid)
                .header("Authorization", token)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), clientResponse -> {
                    log.error("❌ 결제 조회 실패: impUid={}, HTTP={}", impUid, clientResponse.statusCode());
                    return clientResponse.createException();
                })
                .bodyToMono(JsonNode.class)
                .block();

        if (result == null) {
            log.warn("⚠️ 아임포트 결제 조회 결과가 비어있습니다. impUid={}", impUid);
        }

        return result;
    }
}
