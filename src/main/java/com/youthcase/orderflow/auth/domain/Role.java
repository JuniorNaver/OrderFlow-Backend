package com.youthcase.orderflow.auth.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set; // 💡 import 추가

/**
 * 시스템 내에서 사용되는 사용자 역할을 정의하는 엔티티입니다.
 * RoleType Enum의 roleId(String)을 PK로 사용합니다.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "role")
public class Role {

    @Id
    @Column(name = "role_id", nullable = false, length = 50)
    private String roleId;

    @Column(name = "description", length = 255)
    private String description;

    // 🚨 필수 추가: Role과 Authority 간의 N:M을 해소하는 매핑 엔티티와의 관계 (1:N)
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RoleAuthMapping> roleAuthMappings = new HashSet<>(); // 💡 컬렉션 필드 추가

    // 필요하다면, UserRole 매핑 컬렉션 (양방향 연결)도 추가할 수 있습니다.
    // 하지만 CustomUserDetailsService 오류 해결에는 위 RoleAuthMapping만 필요합니다.
}