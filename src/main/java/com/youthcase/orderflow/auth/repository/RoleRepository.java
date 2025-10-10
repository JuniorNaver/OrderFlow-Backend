package com.youthcase.orderflow.auth.repository;

import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    /**
     * RoleType Enum 값을 사용하여 Role 엔티티를 조회합니다.
     * @param roleType RoleType (예: RoleType.ADMIN)
     * @return Role 엔티티 (Optional)
     */
    Optional<Role> findByRoleType(RoleType roleType);

    /**
     * String 타입의 RoleId로 Role 엔티티를 조회합니다.
     */
    Optional<Role> findByRoleId(String roleId);
}

