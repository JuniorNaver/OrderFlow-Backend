package com.youthcase.orderflow.auth.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Arrays; // Arrays 임포트 추가

@Getter
@RequiredArgsConstructor
public enum RoleType {

    ADMIN("ROLE_ADMIN", "최고 관리자 권한"),
    MANAGER("ROLE_MANAGER", "점장 권한"),
    CLERK("ROLE_CLERK", "점원 권한");

    private final String roleId;
    private final String description;

    /**
     * 역할 ID(String)를 받아 해당하는 RoleType Enum 상수를 반환합니다.
     * @param roleId (예: "ROLE_MANAGER")
     * @return 일치하는 RoleType 상수
     * @throws IllegalArgumentException 일치하는 RoleType이 없을 경우
     */
    public static RoleType fromRoleId(String roleId) {
        return Arrays.stream(RoleType.values())
                .filter(roleType -> roleType.getRoleId().equalsIgnoreCase(roleId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Role ID: " + roleId));
    }
}