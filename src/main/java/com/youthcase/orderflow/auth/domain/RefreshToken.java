package com.youthcase.orderflow.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Refresh Token 정보를 저장하는 엔티티.
 * 일반적으로 Redis에 저장하지만, 여기서는 JPA를 사용하여 RDB에 저장합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "REFRESH_TOKEN")
public class RefreshToken {

    // PK: 사용자 ID (User 테이블의 PK가 아님, 논리적 연결을 위한 ID)
    @Id
    @Column(name = "USER_ID", length = 50, nullable = false)
    private String userId; // 이대로 유지하거나, 필드명을 userPkId 등으로 명확히 해도 좋음

    // Refresh Token 값 자체
    @Column(name = "TOKEN", length = 500, nullable = false)
    private String token;

    // 생성자 (빌더)
    @Builder
    public RefreshToken(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    /**
     * Refresh Token을 갱신하는 비즈니스 로직 (토큰 Rotation)
     */
    public void updateToken(String newToken) {
        this.token = newToken;
    }
}