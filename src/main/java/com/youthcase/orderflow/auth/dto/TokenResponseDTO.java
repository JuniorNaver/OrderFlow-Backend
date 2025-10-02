package com.youthcase.orderflow.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponseDTO {
    private String grantType;     // 토큰 타입 (예: "Bearer")
    private String accessToken;   // 접근 토큰
    private String refreshToken;  // 재발급 토큰
    private Long accessTokenExpiresIn; // 접근 토큰 만료 시간 (ms)
}