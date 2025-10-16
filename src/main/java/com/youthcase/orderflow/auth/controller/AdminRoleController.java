package com.youthcase.orderflow.auth.controller;

import com.youthcase.orderflow.auth.dto.RolePermissionDto;
import com.youthcase.orderflow.auth.dto.RolePermissionUpdateDto;
import com.youthcase.orderflow.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    // ⭐ @PreAuthorize("hasRole('ADMIN')") 등을 사용하여 ADMIN만 접근 가능하도록 해야 합니다.
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
    // ⭐ @PreAuthorize("hasRole('ADMIN')") 등을 사용하여 ADMIN만 접근 가능하도록 해야 합니다.
    public ResponseEntity<Void> updateAllRolePermissions(@RequestBody List<RolePermissionUpdateDto> updateList) {
        // RoleService에서 변경 목록을 받아 DB의 Role-Authority 매핑 상태를 일괄 업데이트
        roleService.updateAllRolePermissions(updateList);
        return ResponseEntity.ok().build();
    }
}
