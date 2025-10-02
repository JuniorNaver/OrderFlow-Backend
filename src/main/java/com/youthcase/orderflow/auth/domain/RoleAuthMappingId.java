package com.youthcase.orderflow.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable // 다른 엔티티에 삽입될 수 있는 값 타입
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor // Builder 패턴을 위해 모든 필드를 가진 생성자 필요
@EqualsAndHashCode // 복합 키의 동등성 비교를 위해 필수
public class RoleAuthMappingId implements Serializable {

    // 역할ID (ROLE_ID) - ROLE 테이블의 PK를 참조 (String 타입)
    @Column(name = "ROLE_ID", length = 50, nullable = false)
    private String roleId;

    // 권한ID (AUTHORITY_ID) - AUTHORITY 테이블의 PK를 참조 (Long 타입)
    // 매핑 테이블의 컬럼 이름은 AUTHORITY_ID이지만, 실제 JPA 필드 이름은 Authority 엔티티의 ID 타입과 일치시켜 Long을 사용합니다.
    @Column(name = "AUTHORITY_ID", nullable = false)
    private Long authorityId;
}