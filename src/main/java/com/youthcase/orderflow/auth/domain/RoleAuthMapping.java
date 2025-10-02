package com.youthcase.orderflow.auth.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ROLE_AUTH_MAPPING")
public class RoleAuthMapping {

    // 복합 키를 정의합니다.
    @EmbeddedId
    private RoleAuthMappingId id;

    // 1. ROLE 엔티티와의 관계 (다대일)
    // id 필드의 'roleId' 부분과 매핑합니다.
    @ManyToOne
    @MapsId("roleId") // RoleAuthMappingId의 roleId 필드와 매핑
    @JoinColumn(name = "ROLE_ID")
    private Role role;

    // 2. AUTHORITY 엔티티와의 관계 (다대일)
    // id 필드의 'authorityId' 부분과 매핑합니다.
    @ManyToOne
    @MapsId("authorityId") // RoleAuthMappingId의 authorityId 필드와 매핑
    @JoinColumn(name = "AUTHORITY_ID")
    private Authority authority;

    // --- 생성자/빌더 ---

    @Builder
    public RoleAuthMapping(Role role, Authority authority) {
        // 엔티티를 받아서 복합 키를 생성합니다.
        this.id = new RoleAuthMappingId(role.getRoleId(), authority.getAuthorityId());
        this.role = role;
        this.authority = authority;
    }
}