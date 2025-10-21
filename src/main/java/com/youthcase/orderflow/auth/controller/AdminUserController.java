package com.youthcase.orderflow.auth.controller;

import com.youthcase.orderflow.auth.dto.AccountAdminResponseDTO;
import com.youthcase.orderflow.auth.dto.AccountCreateRequestDTO;
import com.youthcase.orderflow.auth.dto.AccountUpdateRequestDTO;
import com.youthcase.orderflow.auth.dto.UserBatchDeleteRequestDTO; // 💡 사용할 DTO import
import com.youthcase.orderflow.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 관리자 전용 사용자(계정) 관리 REST Controller
 * 경로: /api/admin/users
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    // [GET] 계정 목록 조회 및 검색
    @PreAuthorize("hasRole('ADMIN')") // 모든 관리자 API에 ADMIN 권한 요구
    @GetMapping
    public ResponseEntity<List<AccountAdminResponseDTO>> getAdminUserList(
            @RequestParam(required = false) String search) {

        List<AccountAdminResponseDTO> users = userService.findAllUsersForAdmin(search);
        return ResponseEntity.ok(users);
    }

    // [POST] 계정 생성
    @PreAuthorize("hasRole('ADMIN')") // 모든 관리자 API에 ADMIN 권한 요구
    @PostMapping
    public ResponseEntity<AccountAdminResponseDTO> createAccount(
            @Valid @RequestBody AccountCreateRequestDTO request) {

        AccountAdminResponseDTO newUser = userService.createAccountByAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    // [PUT] 계정 정보 수정
    @PreAuthorize("hasRole('ADMIN')") // 모든 관리자 API에 ADMIN 권한 요구
    @PutMapping("/{userId}")
    public ResponseEntity<AccountAdminResponseDTO> updateAccount(
            @PathVariable String userId,
            @Valid @RequestBody AccountUpdateRequestDTO request) {

        AccountAdminResponseDTO updatedUser = userService.updateAccountByAdmin(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    // 💡 [DELETE] 계정 일괄 삭제 (UserBatchDeleteRequestDTO 활용)
    @PreAuthorize("hasRole('ADMIN')") // 모든 관리자 API에 ADMIN 권한 요구
    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteAccountsBatch(
            @Valid @RequestBody UserBatchDeleteRequestDTO request) {

        // Service에 DTO를 전달하여 삭제 로직을 실행
        userService.deleteUsersBatch(request.getUserIds());

        return ResponseEntity.noContent().build();
    }

    // [DELETE] 개별 계정 삭제 (Path Variable 사용)
    @PreAuthorize("hasRole('ADMIN')") // 모든 관리자 API에 ADMIN 권한 요구
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String userId) {

        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
