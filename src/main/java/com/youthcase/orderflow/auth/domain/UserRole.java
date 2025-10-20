package com.youthcase.orderflow.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * User와 Role 간의 N:M 관계를 해소하는 매핑 엔티티입니다.
 * 역할 부여 시점(assignedAt)과 같은 추가 속성을 가질 수 있습니다.
 */
@Entity
@Getter // 💡 getRole(), getUser() 메서드 자동 생성 (CustomUserDetailsService 오류 해결)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 요구사항
@AllArgsConstructor
@Table(name = "user_role")
public class UserRole {

    // 단일 ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. User 엔티티와의 관계 (다대일)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // user_role 테이블의 FK
    private User user;

    // 2. Role 엔티티와의 관계 (다대일)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id") // user_role 테이블의 FK
    private Role role;

    // 💡 확장성: 역할 부여 시점 기록
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;


    // --- Builder 생성자 ---
    // @Builder를 클래스 레벨에 사용했으므로 AllArgsConstructor는 생략 가능하며,
    // 필요한 필드만 받는 수동 생성자를 정의할 수도 있습니다.

    @Builder
    public UserRole(User user, Role role, LocalDateTime assignedAt) {
        this.user = user;
        this.role = role;
        // assignedAt이 명시되지 않으면 현재 시점으로 기본 설정
        this.assignedAt = (assignedAt != null) ? assignedAt : LocalDateTime.now();
    }
}