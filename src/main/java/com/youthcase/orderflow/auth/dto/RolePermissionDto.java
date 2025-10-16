package com.youthcase.orderflow.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * 역할 목록 및 현재 권한 상태를 프론트엔드에 전달하기 위한 DTO (GET 응답용)
 */
@Getter
@Builder
public class RolePermissionDto {

    // Spring Security의 역할 ID (예: "ROLE_MANAGER")
    private final String roleId;

    // 사용자 친화적인 역할명 (예: "점장")
    private final String position;

    // 권한 키와 활성화 상태의 매핑 (예: { "PO": true, "PR": false, ... })
    private final Map<String, Boolean> permissions;
}
