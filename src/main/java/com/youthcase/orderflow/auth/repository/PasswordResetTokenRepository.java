package com.youthcase.orderflow.auth.repository;

import com.youthcase.orderflow.auth.domain.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * 토큰 값(String token)으로 PasswordResetToken 엔티티를 조회합니다.
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * 사용자 ID로 아직 사용되지 않은 유효한 토큰을 조회합니다. (선택적)
     * 이 메서드를 사용하여 사용자가 여러 개의 초기화 토큰을 생성하는 것을 방지할 수 있습니다.
     */
    Optional<PasswordResetToken> findByUserIdAndUsedFalse(String userId);
}