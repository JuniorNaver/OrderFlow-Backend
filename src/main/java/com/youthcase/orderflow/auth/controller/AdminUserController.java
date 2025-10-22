package com.youthcase.orderflow.auth.controller;

import com.youthcase.orderflow.auth.dto.UserCreateRequestDTO;
import com.youthcase.orderflow.auth.dto.UserResponseDTO;
import com.youthcase.orderflow.auth.dto.UserUpdateRequestDTO;
import com.youthcase.orderflow.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * [관리자용] 사용자 계정(AppUser) 관리 요청을 처리하는 REST Controller.
 * AccountManage.jsx의 API_URL ('/api/admin/users')에 대응합니다.
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    // ----------------------------------------------------------------------------------
    // R (Read) - 계정 목록 조회 및 검색
    // ----------------------------------------------------------------------------------

    /**
     * [R] 계정 목록 조회 및 검색 (GET /api/admin/users?search={term})
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(
            @RequestParam(required = false) String search) {

        List<UserResponseDTO> users = userService.getAllUsers(search);
        return ResponseEntity.ok(users);
    }

    // ----------------------------------------------------------------------------------
    // C (Create) - 새 계정 생성
    // ----------------------------------------------------------------------------------

    /**
     * [C] 새 계정 생성 (POST /api/admin/users)
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreateRequestDTO requestDTO) {
        // UserCreateRequestDTO의 Getter 오류 해결 완료
        UserResponseDTO createdUser = userService.createUser(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // ----------------------------------------------------------------------------------
    // U (Update) - 특정 계정 수정
    // ----------------------------------------------------------------------------------

    /**
     * [U] 특정 계정 수정 (PUT /api/admin/users/{userId})
     * 💡 String 타입의 userId(PK)를 사용하도록 수정되었습니다.
     *
     * @param userId 수정할 계정의 PK (String)
     * @param requestDTO 수정 요청 데이터 (UserUpdateRequestDTO의 Getter 오류 해결 완료)
     * @return 수정된 계정의 상세 정보
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable String userId,
            @RequestBody UserUpdateRequestDTO requestDTO) {

        UserResponseDTO updatedUser = userService.updateUser(userId, requestDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // ----------------------------------------------------------------------------------
    // D (Delete) - 삭제
    // ----------------------------------------------------------------------------------

    /**
     * [D] 특정 계정 삭제 (DELETE /api/admin/users/{userId}) 👈 단일 삭제 엔드포인트 추가
     * * @param userId 삭제할 계정의 PK (String)
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{userId}") // 👈 프론트엔드 AccountManage.jsx의 단일 삭제 요청에 대응
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        // UserService의 단일 삭제 메서드를 호출합니다.
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * [D] 선택된 계정 일괄 삭제 (DELETE /api/admin/users/batch)
     * 💡 String 타입의 userId 목록을 받도록 수정되었습니다.
     *
     * @param body IDs 목록을 담고 있는 Map (예: {"ids": ["user1", "user2", "user3"]})
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteUsers(@RequestBody Map<String, List<String>> body) {
        List<String> userIds = body.get("ids");
        if (userIds == null || userIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // UserService의 일괄 삭제 메서드를 호출합니다.
        userService.deleteUsers(userIds);
        return ResponseEntity.noContent().build();
    }
}