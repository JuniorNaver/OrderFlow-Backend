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

    // ✅ Role 기준으로 User 1:N 관계 추가
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private Set<User> users = new HashSet<>();

    // 🚨 필수 추가: Role과 Authority 간의 N:M을 해소하는 매핑 엔티티와의 관계 (1:N)
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RoleAuthMapping> roleAuthMappings = new HashSet<>(); // 💡 컬렉션 필드 추가
}