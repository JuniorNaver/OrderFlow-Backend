package com.youthcase.orderflow.auth.repository;

import com.youthcase.orderflow.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // JpaRepository<[엔티티 타입: User], [PK 타입: String]>

    /**
     * 사용자 ID(userId)로 사용자를 조회합니다.
     * 사용자 인증(로그인) 시 필요한 핵심 메서드입니다.
     * @param userId 사용자 고유 ID
     * @return User 엔티티 (Optional)
     */
    Optional<User> findByUserId(String userId);

    /**
     * 이메일(email)을 사용하여 사용자를 조회합니다.
     * 이메일 중복 확인 또는 비밀번호 찾기 기능에 유용합니다.
     * @param email 사용자 이메일
     * @return User 엔티티 (Optional)
     */
    Optional<User> findByEmail(String email);

    /**
     * 사용자 이름(username)을 포함하는 모든 사용자를 조회합니다.
     * 사용자 검색 기능 등에 유용합니다.
     * @param username 검색할 이름의 부분 문자열
     * @return User 엔티티 리스트
     */
    List<User> findByUsernameContaining(String username);
}