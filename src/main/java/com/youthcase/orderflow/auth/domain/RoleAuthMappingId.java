package com.youthcase.orderflow.auth.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

// 복합 키 클래스는 반드시 @Embeddable을 붙이고 Serializable을 구현해야 합니다.
@Embeddable
@Getter
@EqualsAndHashCode // 복합 키이므로 equals/hashCode 구현은 필수
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor // Builder나 생성자를 위한 AllArgsConstructor
public class RoleAuthMappingId implements Serializable {

    // 역할 ID (Role 엔티티의 기본 키를 참조)
    private String roleId;

    // 권한 ID (Authority 엔티티의 기본 키를 참조)
    private Long authorityId;
}
