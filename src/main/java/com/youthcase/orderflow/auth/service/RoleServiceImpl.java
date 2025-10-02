package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.domain.Authority;
import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.RoleAuthMapping;
import com.youthcase.orderflow.auth.domain.RoleAuthMappingId;
import com.youthcase.orderflow.auth.domain.enums.RoleType;
import com.youthcase.orderflow.auth.repository.AuthorityRepository; // 의존성 추가
import com.youthcase.orderflow.auth.repository.RoleAuthMappingRepository; // 의존성 추가
import com.youthcase.orderflow.auth.repository.RoleRepository;
import com.youthcase.orderflow.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository; // 새로 추가
    private final RoleAuthMappingRepository roleAuthMappingRepository; // 새로 추가

    @Override
    public Optional<Role> findByRoleType(RoleType roleType) {
        return roleRepository.findByRole(roleType);
    }

    @Override
    @Transactional // 쓰기 작업
    public void addAuthorityToRole(String roleId, Long authorityId) {

        // 1. 역할(Role)과 권한(Authority) 엔티티가 존재하는지 확인
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("역할을 찾을 수 없습니다. ID: " + roleId));

        Authority authority = authorityRepository.findById(authorityId)
                .orElseThrow(() -> new IllegalArgumentException("권한을 찾을 수 없습니다. ID: " + authorityId));

        // 2. 이미 매핑이 존재하는지 확인 (중복 부여 방지)
        boolean exists = roleAuthMappingRepository.existsById_RoleIdAndId_AuthorityId(roleId, authorityId);
        if (exists) {
            // 이미 매핑되어 있다면 예외 발생 또는 무시
            throw new IllegalArgumentException("이미 해당 역할에 부여된 권한입니다.");
        }

        // 3. 매핑 엔티티 생성 및 저장
        RoleAuthMapping mapping = RoleAuthMapping.builder()
                .role(role)
                .authority(authority)
                .build();

        roleAuthMappingRepository.save(mapping);
    }

    @Override
    @Transactional // 쓰기 작업
    public void removeAuthorityFromRole(String roleId, Long authorityId) {

        // 1. 삭제할 매핑의 복합 키(ID) 생성
        RoleAuthMappingId mappingId = new RoleAuthMappingId(roleId, authorityId);

        // 2. 해당 매핑 엔티티가 존재하는지 확인
        RoleAuthMapping mapping = roleAuthMappingRepository.findById(mappingId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 역할-권한 매핑을 찾을 수 없습니다."));

        // 3. 매핑 삭제
        roleAuthMappingRepository.delete(mapping);
    }
}