package com.youthcase.orderflow.auth.domain;

import com.youthcase.orderflow.auth.domain.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * 사용자에게 부여되는 역할(Group of Authorities)을 정의하는 엔티티입니다.
 * 예: ADMIN, MANAGER, USER 등
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "role")
public class Role {

    // 역할 ID (문자열, 예: ROLE_ADMIN)
    @Id
    @Column(name = "role_id", length = 50)
    private String roleId;

    // 역할 타입 (Enum을 사용하여 정의된 역할 목록 관리) / Enum 이름: ADMIN, MANAGER, CLERK
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false, unique = true, length = 50)
    private RoleType roleType;

    // 역할 설명
    @Column(name = "description", length = 255)
    private String description;

    // 이 역할이 가지고 있는 RoleAuthority 매핑 (양방향 매핑, 읽기 전용)
    // 🚨 수정: RoleAuthority 대신 RoleAuthMapping 사용 (에러 해결)
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RoleAuthMapping> roleAuthMappings = new HashSet<>(); // 필드명도 변경
}