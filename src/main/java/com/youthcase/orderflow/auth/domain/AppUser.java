package com.youthcase.orderflow.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 애플리케이션 사용자 정보를 담는 JPA 엔티티.
 * 로그에서 확인된 app_user 테이블 구조를 기반으로 작성되었습니다.
 */
@Entity
@Table(name = "app_user")
@Getter
@NoArgsConstructor
public class AppUser {

    @Id
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "name")
    private String name;

    // 비밀번호 필드는 보안상 숨김 처리되거나 DTO에 포함되지 않도록 주의해야 합니다.
    // 여기서는 DB 매핑을 위해 포함합니다.
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "workspace")
    private String workspace;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 편의를 위한 생성자 또는 빌더 패턴 등을 추가할 수 있습니다.

    // 참고: 로그에 `user_id` 외에 `created_at, email, enabled, name, password, workspace` 필드가 포함되어 이를 반영했습니다.
}
