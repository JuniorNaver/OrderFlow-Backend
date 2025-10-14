package com.youthcase.orderflow.auth.controller;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.dto.UserResponseDTO;
import com.youthcase.orderflow.auth.dto.UserPasswordChangeRequestDTO;
import com.youthcase.orderflow.auth.service.UserService;
import com.youthcase.orderflow.global.config.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth/users") // 사용자 본인 정보 관리를 위한 기본 경로
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * [GET] 로그인된 사용자 본인의 정보 조회
     * GET /api/auth/users/me
     *
     * @param securityUser 현재 로그인된 사용자 정보 (Spring Security Principal)
     * @return UserResponseDTO
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyDetails(@AuthenticationPrincipal SecurityUser securityUser) {

        String userId = securityUser.getUsername();

        User user = userService.findByUserId(userId)
                // Optional<User>를 반환한다고 가정
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        return ResponseEntity.ok(UserResponseDTO.from(user));
    }

    /**
     * [PUT] 로그인된 사용자 본인의 비밀번호 변경
     * PUT /api/auth/users/password
     *
     * @param securityUser 현재 로그인된 사용자 정보
     * @param request DTO를 사용하여 새 비밀번호 추출
     * @return 204 No Content
     */
    @PutMapping("/password")
    public ResponseEntity<Void> changeMyPassword(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody @Valid UserPasswordChangeRequestDTO request) { // 💡 DTO 사용으로 변경

        String userId = securityUser.getUsername();
        String newPassword = request.getNewPassword();

        // DTO에 @NotBlank가 있으므로, 별도의 null/empty 검사는 불필요합니다.
        // 유효성 검사 실패 시 GlobalExceptionHandler에서 400 Bad Request가 반환됩니다.

        // Service 계층에 비밀번호 변경 로직 위임
        userService.changePassword(userId, newPassword);

        // 성공 시 204 No Content 반환
        return ResponseEntity.noContent().build();
    }
}
