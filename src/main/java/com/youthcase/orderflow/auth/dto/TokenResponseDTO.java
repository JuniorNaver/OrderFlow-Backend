package com.youthcase.orderflow.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponseDTO {

    // 토큰 유형 (예: "Bearer")
    private String grantType;

    // Access Token 값
    private String accessToken;

    // Refresh Token 값
    private String refreshToken;

    // Access Token 만료 시간 (밀리초)
    private long accessTokenExpiresIn;
}
