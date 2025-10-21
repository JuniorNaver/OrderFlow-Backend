package com.youthcase.orderflow.auth.controller;

import com.youthcase.orderflow.auth.dto.*;
import com.youthcase.orderflow.auth.service.AuthService; // 인증 및 토큰 발급 로직을 처리할 서비스
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

        // 🚨 수정: LoginRequestDTO 객체 전체를 서비스로 전달하도록 변경
        TokenResponseDTO tokenResponse = authService.authenticateAndGenerateToken(request);

        return ResponseEntity.ok(tokenResponse);
    }

    // 추후 토큰 재발급, 로그아웃 등의 엔드포인트를 여기에 추가합니다.

    /**
     * [POST] 토큰 재발급 API (RefreshToken 사용)
     * POST /api/auth/reissue
     */

    /**
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

    /**
     * [POST] 사용자 회원가입
     * POST /api/auth/register
     *
     * @param request UserRegisterRequestDTO
     * @return ResponseEntity<Void> (201 Created)
     */
    // AuthController.java (개선)
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
     * [POST] 비밀번호 초기화 요청
     * 사용자 ID와 이메일을 검증하고, 비밀번호 초기화 토큰을 생성하여 이메일로 발송합니다.
     * POST /api/auth/password/reset-request
     *
     * @param request PasswordResetRequestDTO (userId, email)
     * @return 204 No Content
     */
    /**
     * 비밀번호 초기화 이메일 발송을 요청합니다.
     * 클라이언트로부터 받은 DTO에는 userId(또는 이메일)가 포함되어야 합니다.
     * @param request 비밀번호 초기화 요청 DTO (PasswordResetRequestDTO 또는 유사 DTO)
     * @return 성공 응답
     */
    @PostMapping("/password-reset-request")
    public ResponseEntity<Void> requestPasswordReset(@RequestBody PasswordResetRequestDTO request) {

        // 🚨 [수정] 서비스 인터페이스에 정의된 requestPasswordReset(String) 메서드를 호출합니다.
        // 이전에 sendPasswordResetEmail(String, String)을 호출하던 부분을 대체합니다.
        authService.requestPasswordReset(request.getUserId());

        return ResponseEntity.ok().build();
    }


}