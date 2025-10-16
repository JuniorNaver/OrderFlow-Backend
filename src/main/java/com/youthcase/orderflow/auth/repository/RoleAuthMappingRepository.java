package com.youthcase.orderflow.auth.repository;

import com.youthcase.orderflow.auth.domain.Authority;
import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.RoleAuthMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleAuthMappingRepository extends JpaRepository<RoleAuthMapping, Long> {

    /**
     * 역할 ID(roleId)에 매핑된 권한 목록과 관련된 Authority 엔티티를 JOIN FETCH하여 조회합니다.
     * 이를 통해 CustomUserDetailsService에서 발생하는 N+1 문제를 방지합니다.
     */
    @Query("SELECT ram FROM RoleAuthMapping ram JOIN FETCH ram.authority a WHERE ram.role.roleId = :roleId")
    List<RoleAuthMapping> findWithAuthoritiesByRoleId(@Param("roleId") String roleId);

    /**
     * 특정 Role과 Authority 쌍으로 매핑 엔티티의 존재 여부를 확인합니다.
     * (RoleServiceImpl의 addAuthorityToRole에서 사용)
     */
    boolean existsByRoleAndAuthority(Role role, Authority authority);

    /**
     * 특정 Role과 Authority 쌍으로 매핑 엔티티를 조회합니다.
     * (RoleServiceImpl의 removeAuthorityFromRole에서 사용)
     */
    Optional<RoleAuthMapping> findByRoleAndAuthority(Role role, Authority authority);

    /**
     * [신규 추가] 특정 Role 엔티티에 매핑된 모든 RoleAuthMapping 목록을 조회합니다.
     * (RoleServiceImpl의 updateAllRolePermissions에서 사용)
     * @param role 조회할 Role 엔티티
     * @return 해당 Role의 모든 RoleAuthMapping 리스트
     */
    List<RoleAuthMapping> findByRole(Role role);
}
