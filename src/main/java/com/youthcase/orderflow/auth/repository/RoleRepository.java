package com.youthcase.orderflow.auth.repository;

import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    // JpaRepository<[엔티티 타입: Role], [PK 타입: String]>

    /**
     * RoleType Enum 값을 사용하여 Role 엔티티를 조회합니다.
     * @param role RoleType (예: RoleType.ADMIN)
     * @return Role 엔티티 (Optional)
     */
    Optional<Role> findByRole(RoleType role);

    // PK인 roleId (String)를 사용한 조회는 JpaRepository의 findById()가 자동으로 제공합니다.
}