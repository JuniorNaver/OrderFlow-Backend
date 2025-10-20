package com.youthcase.orderflow.auth.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors; // 💡 Collectors import 추가

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

    // 근무지 (UserService에서 사용됨)
    @Column(name = "workspace", length = 100)
    private String workspace;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;

    // 💡 변경: UserRole 엔티티를 참조하는 1:N 관계로 변경됨
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    // --- 비즈니스 로직 지원 메서드 ---

    // UserService.updateUserDetails() 지원 메서드
    public void updateDetails(String name, String workspace, String email) {
        if (name != null) this.name = name;
        if (workspace != null) this.workspace = workspace;
        if (email != null) this.email = email;
    }

    // UserService.changePassword() 지원 메서드
    public void updatePassword(String newHashedPassword) {
        this.password = newHashedPassword;
    }

    // 💡 추가: UserResponseDTO에서 getRoles() 오류를 해결하기 위해 추가된 헬퍼 메서드
    /**
     * UserRole 관계를 통해 연결된 실제 Role 엔티티 목록을 반환합니다.
     */
    public Set<Role> getRoles() {
        return this.userRoles.stream()
                .map(UserRole::getRole) // UserRole 엔티티에 getRole()이 있다고 가정
                .collect(Collectors.toSet());
    }
}