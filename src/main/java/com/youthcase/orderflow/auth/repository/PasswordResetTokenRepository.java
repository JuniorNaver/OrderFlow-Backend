package com.youthcase.orderflow.auth.repository;

import com.youthcase.orderflow.auth.domain.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // ✅ JPA 쿼리 생성 오류를 해결하는 올바른 메서드 이름
    // (PasswordResetToken -> user -> userId 필드 사용)
    Optional<PasswordResetToken> findByUserUserIdAndUsedFalse(String userId);
    /**
     * 토큰 값(String token)으로 아직 사용되지 않은 토큰을 조회합니다.
     * @param token 토큰 문자열
     * @return 사용 가능한 PasswordResetToken
     */
    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);

}