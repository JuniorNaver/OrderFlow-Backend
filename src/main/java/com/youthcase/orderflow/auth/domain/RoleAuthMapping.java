package com.youthcase.orderflow.auth.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ROLE_AUTH_MAPPING")
public class RoleAuthMapping {

    // 복합 키를 정의합니다.
    @EmbeddedId
    private RoleAuthMappingId id;

    // 1. ROLE 엔티티와의 관계 (다대일)
    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "ROLE_ID")
    private Role role;

    // 2. AUTHORITY 엔티티와의 관계 (다대일)
    @ManyToOne
    @MapsId("authorityId")
    @JoinColumn(name = "AUTHORITY_ID")
    private Authority authority;

    // --- 생성자/빌더 ---

    @Builder
    public RoleAuthMapping(Role role, Authority authority) {
        this.role = role;
        this.authority = authority;
        // this.id = new RoleAuthMappingId(role.getRoleId(), authority.getId()); 이 부분은 제거
    }
}