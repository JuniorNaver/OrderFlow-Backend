package com.youthcase.orderflow.auth.domain;

import jakarta.persistence.*;
import lombok.*;

// import java.util.HashSet;
// import java.util.Set;

/**
 * 시스템 내의 개별 권한 (Permission)을 정의하는 엔티티입니다.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "authority")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_id")
    private Long id;

    // 권한 이름 필드를 'authorityName'에서 'authority'로 변경하여 혼동을 줄입니다.
    @Column(name = "authority", nullable = false, unique = true, length = 50)
    private String authority; // 💡 필드 이름을 authority로 수정

    // 권한 설명
    @Column(name = "description", length = 255)
    private String description;

    // 필수 추가: 권한이 적용되는 URL 패턴
    @Column(name = "url_pattern", nullable = true, length = 255)
    private String url;

    // RoleAuthMapping과의 관계 (편의를 위해 주석 처리)
    // @OneToMany(mappedBy = "authority", cascade = CascadeType.ALL, orphanRemoval = true)
    // @Builder.Default
    // private Set<RoleAuthMapping> roleAuthMappings = new HashSet<>();
}