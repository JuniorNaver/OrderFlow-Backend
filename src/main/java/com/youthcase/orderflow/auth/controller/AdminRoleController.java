package com.youthcase.orderflow.auth.controller;

import com.youthcase.orderflow.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 관리자 전용 API이므로 /api/admin/roles 경로를 사용합니다.
@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {

    private final RoleService roleService;

    // --- API 엔드포인트 ---

    /**
     * [POST] 특정 역할에 권한 부여 (매핑 추가)
     * POST /api/admin/roles/{roleId}/authorities/{authorityId}
     *
     * @param roleId 권한을 부여할 역할 ID (예: "ROLE_MANAGER")
     * @param authorityId 부여할 권한 ID
     * @return 201 Created
     */
    @PostMapping("/{roleId}/authorities/{authorityId}")
    // ⭐ 실제로는 @PreAuthorize("hasRole('ADMIN')") 등을 사용하여 ADMIN만 접근 가능하도록 해야 합니다.
    public ResponseEntity<Void> addAuthorityToRole(
            @PathVariable String roleId,
            @PathVariable Long authorityId) {

        roleService.addAuthorityToRole(roleId, authorityId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * [DELETE] 특정 역할의 권한 회수 (매핑 삭제)
     * DELETE /api/admin/roles/{roleId}/authorities/{authorityId}
     *
     * @param roleId 권한을 회수할 역할 ID
     * @param authorityId 회수할 권한 ID
     * @return 204 No Content
     */
    @DeleteMapping("/{roleId}/authorities/{authorityId}")
    // ⭐ 실제로는 @PreAuthorize("hasRole('ADMIN')") 등을 사용하여 ADMIN만 접근 가능하도록 해야 합니다.
    public ResponseEntity<Void> removeAuthorityFromRole(
            @PathVariable String roleId,
            @PathVariable Long authorityId) {

        roleService.removeAuthorityFromRole(roleId, authorityId);
        return ResponseEntity.noContent().build();
    }

    /**
     * [GET] 시스템에 정의된 모든 역할 목록 조회 (관리자용) (⭐ 활성화)
     * GET /api/admin/roles
     * DTO 변환 없이 Role 엔티티 리스트를 반환합니다.
     */
    @GetMapping
    public ResponseEntity<List<?>> getAllRoles() { // 반환 타입을 List<?>로 설정하여 유연성 확보
        return ResponseEntity.ok(roleService.findAllRoles());
    }
}