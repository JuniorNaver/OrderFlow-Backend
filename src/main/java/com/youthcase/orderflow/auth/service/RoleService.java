package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.enums.RoleType;
import java.util.List; // List import 추가
import java.util.Optional;

public interface RoleService {

    /**
     * RoleType으로 Role 엔티티를 조회합니다.
     * @param roleType 조회할 역할 Enum
     * @return Role 엔티티 (Optional)
     */
    Optional<Role> findByRoleType(RoleType roleType);

    /**
     * 시스템에 정의된 모든 역할을 조회합니다. (AdminRoleController에서 사용 예정)
     * @return 모든 Role 엔티티 리스트
     */
    List<Role> findAllRoles();

    /**
     * 특정 역할(Role)에 특정 권한(Authority)을 부여합니다. (매핑 추가)
     * @param roleId 권한을 부여할 역할 ID
     * @param authorityId 부여할 권한 ID
     */
    void addAuthorityToRole(String roleId, Long authorityId);

    /**
     * 특정 역할(Role)에서 특정 권한(Authority)을 회수합니다. (매핑 삭제)
     * @param roleId 권한을 회수할 역할 ID
     * @param authorityId 회수할 권한 ID
     */
    void removeAuthorityFromRole(String roleId, Long authorityId);
}