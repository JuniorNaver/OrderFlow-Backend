package com.youthcase.orderflow.auth.controller;

import com.youthcase.orderflow.auth.dto.LoginRequestDTO;
import com.youthcase.orderflow.auth.dto.ResetPasswordRequestDTO;
import com.youthcase.orderflow.auth.dto.TokenResponseDTO;
import com.youthcase.orderflow.auth.service.AuthService; // 인증 및 토큰 발급 로직을 처리할 서비스
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") // 인증 관련 경로를 관리
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 참고: AuthService는 추후에 SecurityConfig의 AuthenticationManager를 사용하여 인증을 처리하게 됩니다.

    /**
     * [POST] 사용자 로그인 및 JWT 토큰 발급
     * POST /api/auth/login
     *
     * @param request LoginRequestDTO (userId, password)
     * @return TokenResponseDTO (accessToken, refreshToken)
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO request) {

        // 1. AuthService에 인증 로직 위임 및 토큰 생성 요청
        TokenResponseDTO tokenResponse = authService.authenticateAndGenerateToken(
                request.getUserId(),
                request.getPassword()
        );

        // 2. 생성된 토큰 응답 (200 OK)
        return ResponseEntity.ok(tokenResponse);
    }

    // 추후 토큰 재발급, 로그아웃 등의 엔드포인트를 여기에 추가합니다.

    /**
     * [POST] 토큰 재발급 API (RefreshToken 사용)
     * POST /api/auth/reissue
     */
    /*
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDTO> reissueToken(@RequestBody TokenRequestDTO request) {
        TokenResponseDTO tokenResponse = authService.reissueToken(request.getRefreshToken());
        return ResponseEntity.ok(tokenResponse);
    }
    */

    /**
     * [GET] 비밀번호 초기화 토큰의 유효성을 검증합니다.
     * GET /api/auth/password/validate-token?token={tokenValue}
     */
    @GetMapping("/password/validate-token")
    public ResponseEntity<String> validatePasswordResetToken(@RequestParam String token) {

        // 토큰 유효성 검사 (실패 시 GlobalExceptionHandler에서 400 처리)
        String userId = authService.validatePasswordResetToken(token);

        // 유효성 검사 성공 시, 사용자 ID를 반환하거나 성공 상태만 반환합니다.
        // 여기서는 성공 상태만 반환합니다.
        return ResponseEntity.ok("토큰이 유효합니다.");
    }

    /**
     * [POST] 유효한 토큰과 함께 새 비밀번호를 받아 비밀번호를 재설정합니다.
     * POST /api/auth/password/reset
     */
    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequestDTO request) {

        // 만약 getToken()에서 오류가 난다면...
        authService.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.noContent().build();
    }
}