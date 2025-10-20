package com.youthcase.orderflow.master.store.service.kakaoapi;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KakaoAddressResponse {

    private List<Document> documents;

    @Getter
    @Setter
    public static class Document {
        private Address address;
    }

    @Getter
    @Setter
    public static class Address {
        private String address_name;          // 전체 주소명
        private String region_1depth_name;    // 시/도
        private String region_2depth_name;    // 구/군
        private String region_3depth_name;    // 동/리
        private String region_3depth_h_name;  // 행정동 (optional)
        private String region_type;           // REGION, ROAD 등
        private String x;                     // 경도 (longitude)
        private String y;                     // 위도 (latitude)
    }
}
