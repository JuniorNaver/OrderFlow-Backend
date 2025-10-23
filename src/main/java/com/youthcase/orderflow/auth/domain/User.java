package com.youthcase.orderflow.auth.domain;

import com.youthcase.orderflow.master.store.domain.Store;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
    private String userId;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "email", length = 100)
    private String email;

    // ✅ 지점
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Comment("소속 지점 (STORE_MASTER 참조)")
    private Store store;

    // ✅ 역할 (Role과 직접 연결)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID") // FK 추가
    @Comment("사용자 역할 (ROLE 테이블 참조)")
    private Role role;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // --- 비즈니스 로직 ---

    public void updateDetails(String name, String email, Store store, Role role) {
        if (name != null) this.name = name;
        if (email != null) this.email = email;
        if (store != null) this.store = store;
        if (role != null) this.role = role;
    }

    public void updatePassword(String newHashedPassword) {
        this.password = newHashedPassword;
    }
}