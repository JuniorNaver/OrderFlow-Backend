package com.youthcase.orderflow.auth.domain;

import com.youthcase.orderflow.auth.domain.enums.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ROLE") // 매핑할 테이블 이름
public class Role {

    // 역할ID (ROLE_ID) - PK
    // Enum의 roleId 값을 사용합니다.
    @Id
    @Column(name = "ROLE_ID", length = 50, nullable = false)
    private String roleId;

    // 역할명 (ROLE) - Enum 타입 사용
    // DB에는 Enum의 이름(ADMIN, MANAGER, CLERK)이 문자열로 저장됩니다.
    @Enumerated(EnumType.STRING) // Enum의 이름을 DB에 문자열로 저장하도록 설정
    @Column(name = "ROLE", length = 50, nullable = false, unique = true)
    private RoleType role; // Enum 타입 필드

    // --- 생성자/빌더 ---

    @Builder
    public Role(RoleType role) {
        this.role = role;
        // PK는 Enum 값의 roleId로 설정
        this.roleId = role.getRoleId();
    }

    // --- 비즈니스 로직 (필요시 추가) ---
}