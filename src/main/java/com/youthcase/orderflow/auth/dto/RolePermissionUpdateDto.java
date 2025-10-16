package com.youthcase.orderflow.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 역할별 권한 변경 사항을 백엔드가 수신하기 위한 DTO (PUT 요청용)
 */
@Getter
@Setter
public class RolePermissionUpdateDto {

    // Spring Security의 역할 ID (예: "ROLE_MANAGER")
    private String roleId;

    // 변경된 권한 키와 활성화 상태의 매핑 (예: { "PO": true, "PR": true, ... })
    private Map<String, Boolean> permissions;
}
