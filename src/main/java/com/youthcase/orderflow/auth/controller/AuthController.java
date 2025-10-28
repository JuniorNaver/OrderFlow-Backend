package com.youthcase.orderflow.auth.controller;

import com.youthcase.orderflow.auth.dto.*;
import com.youthcase.orderflow.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth") // 인증 관련 경로를 관리
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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

        authService.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.noContent().build();
    }

    /**
     * [POST] 사용자 회원가입
     * POST /api/auth/register
     *
     * @param request UserRegisterRequestDTO
     * @return ResponseEntity<Void> (201 Created)
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserRegisterRequestDTO request) {
        String userId = authService.registerNewUser(request); // userId 반환하도록 Service 수정 가정

        // 생성된 리소스의 위치(Location)를 헤더에 포함 (예: /api/auth/users/{userId})
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userId) // 생성된 User ID를 사용
                .toUri();

        return ResponseEntity.created(location).build(); // 201 Created + Location 헤더
    }

    /**
     * [POST] 토큰 재발급 API (RefreshToken 사용)
     * POST /api/auth/reissue
     */
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDTO> reissueToken(@RequestBody TokenRequestDTO request) {
        // request.getRefreshToken() 호출
        TokenResponseDTO tokenResponse = authService.reissueToken(request.getRefreshToken());
        return ResponseEntity.ok(tokenResponse);
    }

    /**
     * [POST] 비밀번호 초기화 이메일 요청 API 활성화
     * POST /api/auth/password/reset-request
     * * @param request PasswordResetRequestDTO (userId, email)
     * @return 204 No Content
     */
    @PostMapping("/password/reset-request")
    public ResponseEntity<Void> requestPasswordReset(@RequestBody PasswordResetRequestDTO request) {

        // ⭐️ 오류 해결: 메서드 이름을 AuthService 인터페이스에 맞게 수정
        authService.requestPasswordReset(request.getUserId(), request.getEmail());

        return ResponseEntity.noContent().build(); // 204 No Content
    }

}
