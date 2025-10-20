package com.youthcase.orderflow.auth.repository;

import com.youthcase.orderflow.auth.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    /**
     * RoleType Enum 값을 사용하여 Role 엔티티를 조회합니다.
     * 🚨 Role 엔티티에 roleType 필드가 제거되었으므로, 이 메서드는 제거하거나,
     * findByRoleId로 대체해야 합니다.
     * Optional<Role> findByRoleType(RoleType roleType);
     */

    /**
     * String 타입의 RoleId로 Role 엔티티를 조회합니다.
     */
    Optional<Role> findByRoleId(String roleId);
}