package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.domain.Authority;
import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.RoleAuthMapping;
import com.youthcase.orderflow.auth.domain.enums.RoleType;
import com.youthcase.orderflow.auth.dto.RolePermissionDto;           // DTO 임포트 추가
import com.youthcase.orderflow.auth.dto.RolePermissionUpdateDto;     // DTO 임포트 추가
import com.youthcase.orderflow.auth.repository.AuthorityRepository;
import com.youthcase.orderflow.auth.repository.RoleAuthMappingRepository;
import com.youthcase.orderflow.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final RoleAuthMappingRepository roleAuthMappingRepository;

    @Override
    public Optional<Role> findByRoleType(RoleType roleType) {
        // 💡 수정: Role 엔티티에 RoleType 필드가 없고 RoleRepository에서 해당 메서드가 제거되었으므로,
        // RoleType의 getRoleId()를 사용하여 findByRoleId를 호출합니다.
        return roleRepository.findByRoleId(roleType.getRoleId());
    }

    @Override
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional
    public void addAuthorityToRole(String roleId, Long authorityId) {

        Role role = roleRepository.findByRoleId(roleId)
                .orElseThrow(() -> new IllegalArgumentException("역할 ID를 찾을 수 없습니다: " + roleId));

        Authority authority = authorityRepository.findById(authorityId)
                .orElseThrow(() -> new IllegalArgumentException("권한 ID를 찾을 수 없습니다: " + authorityId));

        if (roleAuthMappingRepository.existsByRoleAndAuthority(role, authority)) {
            throw new IllegalArgumentException(
                    String.format("이미 역할(%s)에 권한(%s)이 부여되어 있습니다.", roleId, authority.getAuthority()));
        }

        RoleAuthMapping roleAuthMapping = RoleAuthMapping.builder()
                .role(role)
                .authority(authority)
                .build();

        roleAuthMappingRepository.save(roleAuthMapping);
    }

    @Override
    @Transactional
    public void removeAuthorityFromRole(String roleId, Long authorityId) {

        Role role = roleRepository.findByRoleId(roleId)
                .orElseThrow(() -> new IllegalArgumentException("역할 ID를 찾을 수 없습니다: " + roleId));

        Authority authority = authorityRepository.findById(authorityId)
                .orElseThrow(() -> new IllegalArgumentException("권한 ID를 찾을 수 없습니다: " + authorityId));

        RoleAuthMapping roleAuthMapping = roleAuthMappingRepository.findByRoleAndAuthority(role, authority)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("역할(%s)과 권한(%s) 간의 매핑을 찾을 수 없습니다.", roleId, authority.getAuthority())));

        roleAuthMappingRepository.delete(roleAuthMapping);
    }

    // --- 일괄 처리 메서드 구현 ---

    /**
     * [GET] 시스템에 정의된 모든 역할 목록과 현재 권한 매핑 상태를 조회합니다.
     */
    @Override
    public List<RolePermissionDto> findAllRolePermissions() {
        // 1. 모든 Role 엔티티 조회
        List<Role> allRoles = roleRepository.findAll();
        // 2. 모든 Authority 엔티티 조회 (Authority ID -> Key로 변환 필요)
        List<Authority> allAuthorities = authorityRepository.findAll();
        // 3. 모든 Role-Authority 매핑 정보 조회
        List<RoleAuthMapping> allMappings = roleAuthMappingRepository.findAll();

        // Authority ID를 Authority Key(PO, PR 등)로 변환하는 Map을 생성해야 합니다.
        // 여기서는 Authority 엔티티가 getAuthorityKey()와 같은 메서드를 가진다고 가정합니다.

        return allRoles.stream()
                .map(role -> {
                    // 해당 Role에 매핑된 Authority Key Set을 DB에서 조회합니다.
                    // 예시: authorityKeySet = getAuthorityKeysForRole(role);

                    Map<String, Boolean> permissions = new HashMap<>();
                    // 모든 권한 키를 순회하며 현재 Role이 해당 권한을 가지고 있는지 체크
                    // 이 로직은 실제 DB 쿼리 효율성을 고려하여 개선되어야 합니다.
                    for (Authority authority : allAuthorities) {
                        // 현재 Role과 Authority 간의 매핑이 존재하면 true, 아니면 false
                        boolean hasPermission = allMappings.stream()
                                .anyMatch(mapping ->
                                        mapping.getRole().equals(role) && mapping.getAuthority().equals(authority));

                        // Authority 엔티티에 getAuthorityKey() 메서드가 있다고 가정
                        permissions.put(authority.getAuthority(), hasPermission);
                    }

                    // RoleType에서 Description을 가져오거나 Role 엔티티의 description 필드를 사용합니다.
                    String positionName = RoleType.fromRoleId(role.getRoleId()).getDescription(); // RoleType Enum 사용 가정

                    return RolePermissionDto.builder()
                            .roleId(role.getRoleId())
                            .position(positionName)
                            .permissions(permissions)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * [PUT] 프론트엔드에서 전달된 역할-권한 변경 목록을 일괄적으로 DB에 반영합니다.
     */
    @Override
    @Transactional
    public void updateAllRolePermissions(List<RolePermissionUpdateDto> updateList) {

        // 1. 모든 Authority 엔티티를 한 번에 조회하여 Map<Authority Key, Authority ID> 형태로 캐시합니다.
        Map<String, Authority> authorityKeyToEntityMap = authorityRepository.findAll().stream()
                .collect(Collectors.toMap(Authority::getAuthority, authority -> authority)); // getAuthority()가 권한 키(PO, PR 등)를 반환한다고 가정

        for (RolePermissionUpdateDto updateDto : updateList) {
            String roleId = updateDto.getRoleId();
            Map<String, Boolean> requestedPermissions = updateDto.getPermissions();

            Role role = roleRepository.findByRoleId(roleId)
                    .orElseThrow(() -> new IllegalArgumentException("역할 ID를 찾을 수 없습니다: " + roleId));

            // 현재 Role에 부여된 모든 기존 매핑을 조회
            List<RoleAuthMapping> existingMappings = roleAuthMappingRepository.findByRole(role);

            // 요청된 권한 상태를 순회하며 INSERT/DELETE 결정
            requestedPermissions.forEach((authorityKey, enabled) -> {
                Authority authority = authorityKeyToEntityMap.get(authorityKey);

                if (authority == null) {
                    System.err.println("경고: 알 수 없는 권한 키입니다: " + authorityKey);
                    return;
                }

                // 기존 매핑 중 현재 Authority와 일치하는 것을 찾습니다.
                Optional<RoleAuthMapping> existingMapping = existingMappings.stream()
                        .filter(m -> m.getAuthority().equals(authority))
                        .findFirst();

                if (enabled) {
                    // 1. 요청: 활성화 (true)
                    if (existingMapping.isEmpty()) {
                        // DB에 매핑이 없으면 -> INSERT
                        RoleAuthMapping newMapping = RoleAuthMapping.builder()
                                .role(role)
                                .authority(authority)
                                .build();
                        roleAuthMappingRepository.save(newMapping);
                    }
                    // DB에 이미 있으면 -> 아무것도 하지 않음 (유지)
                } else {
                    // 2. 요청: 비활성화 (false)
                    if (existingMapping.isPresent()) {
                        // DB에 매핑이 있으면 -> DELETE
                        roleAuthMappingRepository.delete(existingMapping.get());
                    }
                    // DB에 이미 없으면 -> 아무것도 하지 않음 (유지)
                }
            });
        }
    }
}
