package com.youthcase.orderflow.auth.repository;

import com.youthcase.orderflow.auth.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * UserRole 엔티티를 관리하는 JPA Repository 입니다.
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    // User와 Role의 매핑 데이터를 조회, 저장, 삭제합니다.
}
