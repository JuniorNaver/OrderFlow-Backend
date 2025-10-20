package com.youthcase.orderflow.master.store.service.kakaoapi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class KakaoGeocodingClient {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public GeoResult getCoordinate(String address) {
        try {
            String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" +
                    UriUtils.encode(address, StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<KakaoAddressResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, KakaoAddressResponse.class);

            if (response.getBody() == null || response.getBody().getDocuments().isEmpty()) {
                return null;
            }

            var addr = response.getBody().getDocuments().get(0).getAddress();
            return new GeoResult(
                    new BigDecimal(addr.getY()),
                    new BigDecimal(addr.getX())
            );
        } catch (Exception e) {
            log.warn("주소 좌표 변환 실패 [{}]: {}", address, e.getMessage());
            return null;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class GeoResult {
        private BigDecimal latitude;
        private BigDecimal longitude;
    }
}
