package com.youthcase.orderflow.auth.repository;

import com.youthcase.orderflow.auth.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    // 주어진 사용자 ID를 가진 모든 UserRole 매핑을 삭제합니다.
    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.user.userId = :userId")
    void deleteByUserId(@Param("userId") String userId);
}
