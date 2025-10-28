package com.youthcase.orderflow.auth.repository;

import com.youthcase.orderflow.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// 💡 User 엔티티의 PK(userId)가 String이므로 JpaRepository<User, String>를 유지합니다.
public interface UserRepository extends JpaRepository<User, String> {

    @Query("""
            SELECT u
            FROM User u
            LEFT JOIN FETCH u.role r
            LEFT JOIN FETCH u.store s
            WHERE u.userId = :userId
            """)
    Optional<User> findByUserIdWithRoles(@Param("userId") String userId);

    Optional<User> findByUserId(String userId);

    Optional<User> findByEmail(String email);

    // 💡 추가됨: 사용자 ID 또는 이름에 검색어가 포함된 목록을 조회
    List<User> findByUserIdContainingOrNameContaining(String userId, String name);

    boolean existsByUserId(String userId);

    Optional<User> findByUserIdAndEmail(String userId, String email);
}
