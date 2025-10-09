package com.youthcase.orderflow.auth.service.security;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.domain.Role; // Role 엔티티 임포트
import com.youthcase.orderflow.auth.domain.RoleAuthMapping;
import com.youthcase.orderflow.auth.repository.UserRepository;
import com.youthcase.orderflow.auth.repository.RoleAuthMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set; // Set 임포트
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleAuthMappingRepository roleAuthMappingRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        // 🚨 수정 1: User 엔티티에서 유일한 Role ID를 추출합니다.
        // (사용자는 단 하나의 Role만 가진다고 가정합니다.)
        String roleId = user.getRoles().stream()
                .findFirst() // 첫 번째 Role을 가져옴
                .map(Role::getRoleId) // Role 엔티티에서 Role ID(예: ROLE_ADMIN) 추출
                .orElseThrow(() -> new UsernameNotFoundException("사용자에게 할당된 역할(Role)이 없습니다."));


        // 2. 해당 Role ID로 부여된 모든 권한 매핑 정보를 조회
        // 🚨 수정 2: findWithAuthoritiesByRoleId는 Role 엔티티가 아닌 String roleId를 받아야 합니다.
        List<RoleAuthMapping> mappings = roleAuthMappingRepository.findWithAuthoritiesByRoleId(roleId);

        // 3. 권한 매핑 정보와 역할 ID를 기반으로 Spring Security의 권한(Authorities) 리스트 생성
        Collection<GrantedAuthority> authorities = getAuthorities(roleId, mappings);

        // 4. Spring Security의 UserDetails 객체로 변환하여 반환
        return new org.springframework.security.core.userdetails.User(
                user.getUserId(),
                user.getPassword(),
                authorities
        );
    }

    /**
     * 사용자에게 부여된 역할(Role)과 권한(Authority)을 Spring Security 권한 객체로 변환합니다.
     * (메서드 시그니처 변경: User 엔티티 대신 roleId를 받도록 변경)
     */
    private Collection<GrantedAuthority> getAuthorities(String roleId, List<RoleAuthMapping> mappings) {

        // 1. 매핑된 권한(Authority)을 추가
        List<GrantedAuthority> authorityList = mappings.stream()
                // 💡 Authority 엔티티에 getAuthority() 메서드가 있다고 가정
                .map(mapping -> mapping.getAuthority().getAuthority())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 2. 역할(Role) 자체를 권한으로 추가 (Role ID는 이미 "ROLE_" 접두사를 포함한다고 가정)
        authorityList.add(new SimpleGrantedAuthority(roleId));

        return authorityList;
    }
}