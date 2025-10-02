package com.youthcase.orderflow.auth.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum RoleType {

    ADMIN("ROLE_ADMIN", "최고 관리자 권한"),
    MANAGER("ROLE_MANAGER", "점장 권한"),
    CLERK("ROLE_CLERK", "점원 권한");

    // DB에 저장될 실제 역할 ID (예: Spring Security에서 사용하는 'ROLE_' 접두사)
    private final String roleId;

    // 역할에 대한 설명
    private final String description;
}