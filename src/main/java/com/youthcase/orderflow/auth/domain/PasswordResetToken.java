package com.youthcase.orderflow.auth.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "PASSWORD_RESET_TOKEN") // 토큰을 저장할 테이블 이름
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 실제 비밀번호 초기화에 사용될 토큰 값 (UUID 형태)
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    // 토큰을 요청한 사용자 ID (User 엔티티 대신 ID만 참조)
    // 실제 운영에서는 User 엔티티와 @ManyToOne 관계로 연결하는 것이 더 일반적입니다.
    @Column(name = "user_id", nullable = false)
    private String userId;

    // 토큰 만료 시간
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    // 토큰 사용 여부 플래그 (토큰이 이미 사용되었는지 확인)
    @Column(name = "used", nullable = false)
    private boolean used;

    @Builder
    public PasswordResetToken(String token, String userId, LocalDateTime expiryDate) {
        this.token = token;
        this.userId = userId;
        this.expiryDate = expiryDate;
        this.used = false;
    }

    /**
     * 토큰 사용 완료 처리
     */
    public void useToken() {
        this.used = true;
    }

    /**
     * 토큰 만료 여부 확인
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}