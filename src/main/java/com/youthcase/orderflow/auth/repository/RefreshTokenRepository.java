package com.youthcase.orderflow.auth.repository;

import com.youthcase.orderflow.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    /**
     * Refresh Token 문자열 값으로 엔티티를 조회합니다.
     * @param token Refresh Token 문자열
     * @return RefreshToken 엔티티 (Optional)
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * 사용자 ID(userId)로 Refresh Token을 조회합니다.
     * @param userId 사용자 고유 ID
     * @return RefreshToken 엔티티 (Optional)
     */
    Optional<RefreshToken> findByUserId(String userId);

}