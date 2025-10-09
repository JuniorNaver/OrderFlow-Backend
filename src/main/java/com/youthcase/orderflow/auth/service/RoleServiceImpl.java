package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.domain.Authority;
import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.RoleAuthMapping;
import com.youthcase.orderflow.auth.domain.enums.RoleType;
import com.youthcase.orderflow.auth.repository.AuthorityRepository;
import com.youthcase.orderflow.auth.repository.RoleAuthMappingRepository;
import com.youthcase.orderflow.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List; // findAllRoles() 때문에 추가
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final RoleAuthMappingRepository roleAuthMappingRepository;

    @Override
    public Optional<Role> findByRoleType(RoleType roleType) {
        return roleRepository.findByRoleType(roleType);
    }

    /**
     * ✅ 신규 구현: RoleService 인터페이스의 findAllRoles() 구현 (에러 해결)
     */
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
            // 🚨 수정: getAuthorityName() 대신 getAuthority() 호출
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
                        // 🚨 수정: getAuthorityName() 대신 getAuthority() 호출
                        String.format("역할(%s)과 권한(%s) 간의 매핑을 찾을 수 없습니다.", roleId, authority.getAuthority())));

        roleAuthMappingRepository.delete(roleAuthMapping);
    }
}