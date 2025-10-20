package com.youthcase.orderflow.auth.repository;

import com.youthcase.orderflow.auth.domain.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * 특정 사용자 ID(PK)에 대해 아직 사용되지 않은(Used=false) 토큰을 조회합니다.
     * * [쿼리 파싱 경로 설명]
     * findBy -> PasswordResetToken
     * User -> PasswordResetToken 엔티티의 'user' 필드 (ManyToOne 관계)
     * UserId -> User 엔티티의 'userId' 필드 (AppUser의 PK로 예상)
     * AndUsedFalse -> PasswordResetToken 엔티티의 'used' 필드가 false인 조건
     * * @param userId 사용자 엔티티의 기본 키(String 타입으로 가정)
     * @return 사용 가능한 PasswordResetToken
     */
    Optional<PasswordResetToken> findByUserUserIdAndUsedFalse(String userId);

    /**
     * 토큰 값(String token)으로 아직 사용되지 않은 토큰을 조회합니다.
     * @param token 토큰 문자열
     * @return 사용 가능한 PasswordResetToken
     */
    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);
}