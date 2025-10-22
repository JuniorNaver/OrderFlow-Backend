package com.youthcase.orderflow.auth.controller;

import com.youthcase.orderflow.auth.dto.UserResponseDTO;
import com.youthcase.orderflow.auth.dto.UserUpdateRequestDTO;
import com.youthcase.orderflow.auth.service.UserService;
import com.youthcase.orderflow.global.config.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자(AppUser) 관련 요청을 처리하는 REST Controller.
 * Lombok의 @RequiredArgsConstructor를 사용하여 UserService를 자동 주입합니다.
 */
@RestController
@RequestMapping("/api/auth/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ----------------------------------------------------------------------
    // [R] 현재 인증된 사용자 본인의 상세 정보를 조회합니다. (MyPage 정보 조회)
    // 엔드포인트: GET /api/auth/users/me
    // ----------------------------------------------------------------------
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyDetails(
            @AuthenticationPrincipal SecurityUser securityUser) {

        // 🚨 토큰이 있다면 securityUser는 null이 아니어야 합니다.
        if (securityUser == null) {
            // 이 요청은 반드시 토큰이 필요합니다. securityUser가 null이면 401을 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = securityUser.getUsername();

        // ⭐️ [수정] try-catch 제거: 예외 처리를 GlobalExceptionHandler에 위임합니다.
        // UserServiceImpl에서 ResourceNotFoundException을 던지면
        // GlobalExceptionHandler가 이를 404 Not Found로 처리합니다.
        UserResponseDTO responseDTO = userService.getUserDetails(userId);
        return ResponseEntity.ok(responseDTO);
    }

    // ----------------------------------------------------------------------
    // [U] 현재 인증된 사용자 본인의 상세 정보를 수정합니다. (MyPage 정보 수정)
    // ----------------------------------------------------------------------
    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMyDetails(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody UserUpdateRequestDTO requestDTO) {

        // 🚨 주의: NullPointerException 방지용 임시 코드 (실제 운영 환경에서는 필터가 처리)
        // securityUser가 null이면 필터에서 UNAUTHORIZED로 막히므로, 이 코드는 사실상 불필요합니다.
        if (securityUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = securityUser.getUsername();

        // 서비스 계층 호출
        // 비밀번호 불일치 시 UserServiceImple에서 던진 IllegalArgumentException은
        // GlobalExceptionHandler에서 400 Bad Request로 처리됩니다.
        UserResponseDTO responseDTO = userService.updateMyDetails(userId, requestDTO);

        return ResponseEntity.ok(responseDTO);
    }
}
