package com.youthcase.orderflow.auth.controller;

import com.youthcase.orderflow.auth.dto.UserResponseDTO;
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

    /**
     * [주요 수정 부분] 현재 인증된 사용자 본인의 상세 정보를 조회합니다.
     * 엔드포인트: GET /api/auth/users/me
     *
     * @param securityUser Spring Security Context에서 주입된 현재 인증 정보 객체
     * @return UserResponseDTO를 포함하는 ResponseEntity (HTTP 200 OK)
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyDetails(
            @AuthenticationPrincipal SecurityUser securityUser) {

        // 💡 NullPointerException 방지를 위한 안전 체크
        // SecurityUser가 null인 경우, JWT 필터에서 인증 처리가 제대로 되지 않았거나
        // 인증되지 않은 사용자가 보안된 엔드포인트에 접근했다는 의미입니다.
        if (securityUser == null) {
            // 이 경로는 Security Config에 의해 보통 차단되지만, 방어적인 코드를 작성합니다.
            // 실제 환경에서는 Security Filter 체인이 401 Unauthorized를 반환해야 합니다.
            // 명시적인 예외를 던져서 Global Exception Handler가 처리하도록 유도할 수 있습니다.
            // 여기서는 단순화하여 401 응답 코드를 반환합니다.
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // 401 Unauthorized
                    .build();
        }

        // 32번 라인으로 추정되는 securityUser.getUsername() 호출 전에 null 체크가 추가되어 안전합니다.
        String userId = securityUser.getUsername();

        // 서비스 계층을 통해 사용자 상세 정보를 조회합니다.
        UserResponseDTO responseDTO = userService.getUserDetails(userId);

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 사용자 등록 (회원가입) 엔드포인트 예시
     * @param requestDTO 사용자 등록 요청 데이터
     * @return 성공 응답
     */
    // @PostMapping("/register")
    // public ResponseEntity<Void> registerUser(@RequestBody UserRegisterRequestDTO requestDTO) {
    //     // userService.register(requestDTO);
    //     return ResponseEntity.status(HttpStatus.CREATED).build();
    // }

    // ... 여기에 다른 사용자 관련 메서드 (업데이트, 삭제 등)를 추가할 수 있습니다.
}
