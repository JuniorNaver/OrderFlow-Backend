package com.youthcase.orderflow.auth.repository;

import com.youthcase.orderflow.auth.domain.RoleAuthMapping;
import com.youthcase.orderflow.auth.domain.RoleAuthMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleAuthMappingRepository extends JpaRepository<RoleAuthMapping, RoleAuthMappingId> {
    // JpaRepository<[엔티티 타입: RoleAuthMapping], [PK 타입: RoleAuthMappingId]>

    /**
     * 특정 역할 ID(roleId)에 부여된 모든 권한 매핑 목록을 조회합니다.
     * RoleService에서 해당 역할의 모든 권한을 찾을 때 사용됩니다.
     * @param roleId 역할을 식별하는 ID (ROLE_ID)
     * @return RoleAuthMapping 엔티티 리스트
     */
    List<RoleAuthMapping> findById_RoleId(String roleId);

    /**
     * 특정 권한 ID(authorityId)를 가진 모든 역할 매핑 목록을 조회합니다. (역방향 조회)
     * @param authorityId 권한 ID (AUTHORITY_ID)
     * @return RoleAuthMapping 엔티티 리스트
     */
    List<RoleAuthMapping> findById_AuthorityId(Long authorityId);

    /**
     * 특정 역할 ID와 특정 권한 ID를 가진 매핑이 존재하는지 확인합니다.
     * 권한 부여 전 중복 확인에 사용됩니다.
     * @param roleId 역할 ID
     * @param authorityId 권한 ID
     * @return 존재 여부
     */
    boolean existsById_RoleIdAndId_AuthorityId(String roleId, Long authorityId);
}