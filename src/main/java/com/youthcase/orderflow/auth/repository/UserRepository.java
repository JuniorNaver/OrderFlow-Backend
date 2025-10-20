package com.youthcase.orderflow.auth.repository;

import com.youthcase.orderflow.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // 💡 수정됨: 필드 이름 'roles' 대신 'userRoles'를 사용하여 Fetch Join을 수행합니다.
    @Query("SELECT u FROM User u JOIN FETCH u.userRoles ur WHERE u.userId = :userId")
    Optional<User> findByUserIdWithRoles(@Param("userId") String userId);

    Optional<User> findByUserId(String userId);

    Optional<User> findByEmail(String email);

    List<User> findByNameContaining(String name);

    boolean existsByUserId(String userId);
}
