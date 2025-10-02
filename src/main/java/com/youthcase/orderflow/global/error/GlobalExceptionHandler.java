package com.youthcase.orderflow.global.error;

import com.youthcase.orderflow.auth.dto.ErrorResponseDTO; // 응답 DTO를 재사용하거나 새로 정의합니다.
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 모든 @Controller, @RestController에서 발생하는 예외를 잡아냅니다.
public class GlobalExceptionHandler {

    // 참고: ErrorResponseDTO는 필드가 status, message, path 정도인 간단한 DTO를 가정합니다.

    /**
     * 1. [로그인 인증 예외] 사용자를 찾을 수 없거나 비밀번호가 일치하지 않을 때
     * - UsernameNotFoundException: CustomUserDetailsService에서 발생
     * - BadCredentialsException: AuthenticationManager.authenticate()에서 발생 (비밀번호 불일치)
     */
    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(Exception ex) {

        String message = (ex instanceof UsernameNotFoundException)
                ? "사용자 ID를 찾을 수 없습니다."
                : "비밀번호가 일치하지 않습니다.";

        // 401 Unauthorized 대신, 로그인 API에서는 400 Bad Request를 반환하는 경우도 많습니다.
        // 여기서는 '자격 증명 오류'로 명확히 하기 위해 401로 처리하겠습니다.
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 401 Unauthorized
                .body(new ErrorResponseDTO(HttpStatus.UNAUTHORIZED.value(), message));
    }

    /**
     * 2. [비즈니스 로직 예외] 이미 존재하는 권한/역할을 생성하려 하거나, 존재하지 않는 리소스 접근 시
     * - AuthorityServiceImpl 등에서 throw new IllegalArgumentException(...) 발생 시 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {

        // 400 Bad Request
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    // 3. [기타] JWT 토큰 관련 런타임 예외 처리 (선택적)
    // JWTProvider에서 발생하는 MalformedJwtException, ExpiredJwtException 등은
    // JwtAuthenticationFilter 내부에서 처리되어 401로 반환되므로, 여기서는 불필요할 수 있습니다.
}