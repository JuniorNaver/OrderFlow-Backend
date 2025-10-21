package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.dto.LoginRequestDTO; // 🚨 필수
import com.youthcase.orderflow.auth.dto.TokenResponseDTO;
import com.youthcase.orderflow.auth.dto.UserRegisterRequestDTO;

public interface AuthService {

    /**
     * 사용자 ID, 비밀번호, 워크스페이스를 인증하고, 성공 시 JWT 토큰을 생성하여 반환합니다.
     * 🚨 시그니처를 LoginRequestDTO를 받도록 통일 (이전 오류 1, 2 해결)
     */
    TokenResponseDTO authenticateAndGenerateToken(LoginRequestDTO request);


    /**
     * 비밀번호 초기화 요청을 처리하고, 토큰을 생성하여 사용자 이메일로 발송합니다.
     * 🚨 시그니처를 AuthServiceImpl의 구현과 일치하도록 통일 (이전 오류 3 해결)
     */
    void requestPasswordReset(String userId);


    TokenResponseDTO reissueToken(String refreshToken);

    void resetPassword(String token, String newPassword);

    String validatePasswordResetToken(String token);

    String registerNewUser(UserRegisterRequestDTO request);
}
