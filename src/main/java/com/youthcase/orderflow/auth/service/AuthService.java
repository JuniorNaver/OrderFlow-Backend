package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.dto.TokenResponseDTO;

public interface AuthService {

    /**
     * 사용자 ID와 비밀번호를 인증하고, 성공 시 JWT 토큰을 생성하여 반환합니다.
     * @param userId 사용자 ID
     * @param password 비밀번호
     * @return 발급된 Access Token 및 Refresh Token 정보를 담은 TokenResponseDTO
     */
    TokenResponseDTO authenticateAndGenerateToken(String userId, String password);

    // 추후 토큰 재발급 로직 등을 여기에 추가할 수 있습니다.
    // TokenResponseDTO reissueToken(String refreshToken);
}