package com.youthcase.orderflow.auth.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * 시스템 내의 특정 리소스에 접근할 수 있는 권한(Authority)을 정의합니다.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor // Builder 사용을 위해 필요
@Table(name = "authority")
public class Authority {

    // 💡 ID 생성 전략: IDENTITY (Oracle에서 GENERATED AS IDENTITY 사용)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_id")
    private Long id;

    // 권한 식별자 (예: READ_PRODUCT, WRITE_PRODUCT)
    @Column(name = "authority", nullable = false, length = 50, unique = true)
    private String authority;

    // 권한에 매핑되는 URL 패턴 (예: /products/**)
    // 🚨 이전 오류를 해결하기 위해 'url_pattern'으로 컬럼명을 사용했습니다.
    @Column(name = "url_pattern", length = 255)
    private String urlPattern; // 💡 필드명은 'urlPattern'입니다.

    // 권한 설명
    @Column(name = "description", length = 255)
    private String description;

    // RoleAuthMapping 엔티티와의 관계 (1:N)
    // Authority는 다수의 Role에 매핑될 수 있습니다.
    @OneToMany(mappedBy = "authority", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RoleAuthMapping> roleAuthMappings = new HashSet<>();

    /**
     * AuthorityService에서 호출되는 업데이트 메서드
     *
     * @param authority 새로운 권한명
     * @param urlPattern 새로운 URL 패턴
     */
    public void update(String authority, String urlPattern) {
        this.authority = authority;
        this.urlPattern = urlPattern; // 💡 urlPattern 필드 업데이트
    }
}