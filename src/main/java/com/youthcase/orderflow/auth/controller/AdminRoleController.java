package com.youthcase.orderflow.auth.controller;

import com.youthcase.orderflow.auth.dto.RolePermissionDto;
import com.youthcase.orderflow.auth.dto.RolePermissionUpdateDto;
import com.youthcase.orderflow.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 관리자 전용 API이므로 /api/admin/roles 경로를 사용합니다.
@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {

    private final RoleService roleService;

    // --- 기존 API (개별 매핑) 유지 ---
    // (POST, DELETE 메서드는 편의상 생략하고 핵심 GET, PUT만 보여드립니다.)
    // ...

    /**
     * [GET] 시스템에 정의된 모든 역할 목록 및 현재 권한 매핑 상태 조회 (일괄 조회)
     * GET /api/admin/roles/permissions
     *
     * @return List<RolePermissionDto> 현재 역할별 권한 매핑 상태
     */
    @GetMapping("/permissions")
    @PreAuthorize("hasRole('ADMIN')")  //등을 사용하여 ADMIN만 접근 가능하도록 해야 합니다.
    public ResponseEntity<List<RolePermissionDto>> getAllRolePermissions() {
        // RoleService에서 RoleType과 Authority 매핑 상태를 조회하여 DTO로 변환
        List<RolePermissionDto> dtos = roleService.findAllRolePermissions();
        return ResponseEntity.ok(dtos);
    }

    /**
     * [PUT] 모든 역할의 권한 매핑 상태를 일괄 업데이트 (React의 '저장' 버튼 대응)
     * PUT /api/admin/roles/permissions
     *
     * @param updateList 프론트엔드에서 변경된 역할-권한 매핑 목록
     * @return 200 OK
     */
    @PutMapping("/permissions")
    @PreAuthorize("hasRole('ADMIN')") // 등을 사용하여 ADMIN만 접근 가능하도록 해야 합니다.
    public ResponseEntity<Void> updateAllRolePermissions(@RequestBody List<RolePermissionUpdateDto> updateList) {
        // RoleService에서 변경 목록을 받아 DB의 Role-Authority 매핑 상태를 일괄 업데이트
        roleService.updateAllRolePermissions(updateList);
        return ResponseEntity.ok().build();
    }

    // ----------------------------------------------------------------------------------
    // 💡 [POST/DELETE] 단일 권한 추가/제거 (요청하신 메서드 구현)
    // ----------------------------------------------------------------------------------

    /**
     * POST /api/admin/roles/{roleId}/authority/{authorityId}
     * 특정 역할에 단일 권한을 부여합니다.
     * @param roleId 역할을 식별하는 ID (예: ROLE_MANAGER)
     * @param authorityId 권한 엔티티의 ID (예: 1, 2, 3...)
     */
    @PostMapping("/{roleId}/authority/{authorityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addAuthorityToRole(
            @PathVariable String roleId,
            @PathVariable Long authorityId) {

        try {
            roleService.addAuthorityToRole(roleId, authorityId);
            return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created
        } catch (IllegalArgumentException e) {
            // 역할/권한을 찾을 수 없거나 이미 부여된 경우 400 Bad Request 반환
            // 실제 환경에서는 ErrorResponseDTO를 사용하는 것이 좋습니다.
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /api/admin/roles/{roleId}/authority/{authorityId}
     * 특정 역할에서 단일 권한을 제거합니다.
     * @param roleId 역할을 식별하는 ID
     * @param authorityId 권한 엔티티의 ID
     */
    @DeleteMapping("/{roleId}/authority/{authorityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeAuthorityFromRole(
            @PathVariable String roleId,
            @PathVariable Long authorityId) {

        try {
            roleService.removeAuthorityFromRole(roleId, authorityId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (IllegalArgumentException e) {
            // 매핑을 찾을 수 없는 경우 404 Not Found 또는 400 Bad Request 반환
            return ResponseEntity.notFound().build();
        }
    }
}
