package com.youthcase.orderflow.auth.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 사용자 정보를 담는 엔티티입니다.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "APP_USER")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @Column(name = "user_id", length = 50)
    private String userId; // 사용자 ID (PK)

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "email", length = 100)
    private String email;

    // 💡 필수 추가 필드: 근무지 (UserService에서 사용됨)
    @Column(name = "workspace", length = 100)
    private String workspace;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // UserService.updateUserDetails() 지원 메서드
    public void updateDetails(String name, String workspace, String email) {
        // 비즈니스 규칙에 따라 null 체크 또는 유효성 검사를 추가할 수 있습니다.
        if (name != null) this.name = name;
        if (workspace != null) this.workspace = workspace;
        if (email != null) this.email = email;
    }

    // UserService.changePassword() 지원 메서드
    public void updatePassword(String newHashedPassword) {
        this.password = newHashedPassword;
    }
}
