package com.youthcase.orderflow.auth.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 사용자 정보를 담는 엔티티입니다.
 * - 인증/인가 정보와 관리자 계정 관리 정보를 모두 포함합니다.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor // @Builder 사용을 위해 필요
@Table(name = "APP_USER")
@EntityListeners(AuditingEntityListener.class)
public class User {

    // --- 1. 기본 식별 및 인증 정보 ---
    @Id
    @Column(name = "user_id", length = 50)
    private String userId; // 사용자 ID (PK)

    @Column(name = "password", nullable = false, length = 100)
    private String password; // 해시된 비밀번호

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "email", length = 100)
    private String email;

    // --- 2. 운영 및 계정 상태 정보 ---

    // 근무지
    @Column(name = "workspace", length = 100)
    private String workspace;

    // 점포 ID (OrderFlow 시스템 특성 반영)
    @Column(name = "store_id", length = 50)
    private String storeId;

    // 계정 활성화 상태 (Spring Security isEnabled 지원)
    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;

    // 계정 잠금 상태 (Spring Security isAccountNonLocked 지원)
    @Column(name = "locked", nullable = false)
    @Builder.Default
    private boolean locked = false;

    // --- 3. 시간 및 감사 정보 ---

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 마지막 로그인 시간 (감사 및 비활성 계정 처리)
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // 비밀번호 최종 변경 시간 (비밀번호 만료 정책 적용)
    @Column(name = "password_changed_at")
    @Builder.Default
    private LocalDateTime passwordChangedAt = LocalDateTime.now();

    // --- 4. 관계 매핑 ---

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();


    // --- 5. 비즈니스 로직 지원 메서드 ---

    /**
     * [관리자용] 이름, 이메일, 근무지, 상태 등을 일괄 업데이트합니다.
     */
    public void updateAccountByAdmin(String name, String email, String workspace, String storeId, boolean enabled, boolean locked) {
        if (name != null) this.name = name;
        if (email != null) this.email = email;
        if (workspace != null) this.workspace = workspace;
        if (storeId != null) this.storeId = storeId;

        this.enabled = enabled;
        this.locked = locked;
    }

    /**
     * [일반 사용자] 이름, 이메일, 근무지 등 개인 정보를 업데이트합니다.
     */
    public void updateDetails(String name, String workspace, String email) {
        if (name != null) this.name = name;
        if (workspace != null) this.workspace = workspace;
        if (email != null) this.email = email;
    }

    /**
     * 비밀번호를 변경하고 변경 시간을 기록합니다.
     */
    public void updatePassword(String newHashedPassword) {
        this.password = newHashedPassword;
        this.passwordChangedAt = LocalDateTime.now();
    }

    /**
     * 로그인 성공 시 마지막 로그인 시간을 기록합니다.
     */
    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * UserRole 관계를 통해 연결된 실제 Role 엔티티 목록을 반환합니다. (헬퍼 메서드)
     */
    public Set<Role> getRoles() {
        return this.userRoles.stream()
                .map(UserRole::getRole)
                .collect(Collectors.toSet());
    }
}