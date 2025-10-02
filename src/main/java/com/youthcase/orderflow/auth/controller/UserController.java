package com.youthcase.orderflow.auth.controller;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.dto.UserRegisterRequestDTO; // 요청 DTO 사용
import com.youthcase.orderflow.auth.dto.UserResponseDTO;      // 응답 DTO 사용
import com.youthcase.orderflow.auth.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/users") // 기본 URL 경로 설정
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    /**
     * [POST] 사용자 회원가입 (등록)
     * POST /api/auth/users/register
     *
     * @param request UserRegisterRequestDTO
     * @return UserResponseDTO (201 Created)
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRegisterRequestDTO request) {

        // Service 계층에 등록 로직 위임
        User newUser = userService.registerNewUser(
                request.getUserId(),
                request.getUsername(),
                request.getPassword(),
                request.getWorkspace(),
                request.getEmail(),
                request.getRoleId()
        );

        // 등록된 User 엔티티를 응답 DTO로 변환하여 반환
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserResponseDTO.from(newUser));
    }

    /**
     * [GET] 사용자 ID로 정보 조회
     * GET /api/auth/users/{userId}
     *
     * @param userId 조회할 사용자 ID
     * @return UserResponseDTO
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserDetails(@PathVariable String userId) {

        User user = userService.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // 엔티티를 응답 DTO로 변환하여 반환
        return ResponseEntity.ok(UserResponseDTO.from(user));
    }

    /**
     * [PUT] 사용자 비밀번호 변경
     * PUT /api/auth/users/{userId}/password
     *
     * @param userId 비밀번호를 변경할 사용자 ID
     * @param requestBody JSON 본문에서 새 비밀번호를 추출 (예: {"newPassword": "..."})
     * @return 204 No Content
     */
    @PutMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable String userId,
            @RequestBody Map<String, String> requestBody) {

        String newPassword = requestBody.get("newPassword");
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("새 비밀번호를 입력해야 합니다.");
        }

        // Service 계층에 비밀번호 변경 로직 위임
        userService.changePassword(userId, newPassword);

        // 성공 시 204 No Content 반환
        return ResponseEntity.noContent().build();
    }
}