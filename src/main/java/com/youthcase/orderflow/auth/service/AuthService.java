package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.dto.TokenResponseDTO;
import com.youthcase.orderflow.auth.dto.UserRegisterRequestDTO;

public interface AuthService {

    /**
     * 사용자 ID와 비밀번호를 인증하고, 성공 시 JWT 토큰을 생성하여 반환합니다.
     * @param userId 사용자 ID
     * @param password 비밀번호
     * @return 발급된 Access Token 및 Refresh Token 정보를 담은 TokenResponseDTO
     */
    TokenResponseDTO authenticateAndGenerateToken(String userId, String password);


    TokenResponseDTO reissueToken(String refreshToken);

    /**
     * 비밀번호 초기화 요청을 처리하고, 초기화 토큰을 생성하여 사용자에게 이메일로 발송합니다.
     * @param userId 비밀번호 초기화를 요청한 사용자 ID
     */
    void requestPasswordReset(String userId);

    /**
     * 초기화 토큰의 유효성을 검증하고 해당 사용자 ID를 반환합니다.
     */
    String validatePasswordResetToken(String token);

    /**
     * 유효한 토큰을 사용하여 최종적으로 비밀번호를 새 비밀번호로 업데이트합니다.
     */
    void resetPassword(String token, String newPassword);

    /**
     * [수정] 사용자 회원가입을 처리하고, 생성된 사용자의 ID를 반환합니다.
     * @param request 사용자 등록 요청 DTO
     * @return 생성된 사용자의 ID (String)
     */
    String registerNewUser(UserRegisterRequestDTO request);
}
