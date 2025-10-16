package com.youthcase.orderflow.auth.service.security;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.domain.Role;
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
import java.util.stream.Collectors;
import java.util.Optional;

@Service("customUserDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleAuthMappingRepository roleAuthMappingRepository;
    // private final AuthUserMockData authUserMockData; // 🚨 MockData 의존성 제거

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        // 1. DB에서 사용자 조회 (DB만 사용)
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));


        // 2. User 엔티티에서 유일한 Role ID를 추출합니다.
        // (사용자가 여러 역할을 가질 수 있다면 findFirst() 로직을 수정해야 합니다.)
        String roleId = user.getRoles().stream()
                .findFirst()
                .map(Role::getRoleId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자에게 할당된 역할(Role)이 없습니다."));

        // 3. 해당 Role ID로 부여된 모든 권한 매핑 정보를 조회
        List<RoleAuthMapping> mappings = roleAuthMappingRepository.findWithAuthoritiesByRoleId(roleId);

        // 4. 권한 매핑 정보와 역할 ID를 기반으로 Spring Security의 권한(Authorities) 리스트 생성
        Collection<GrantedAuthority> authorities = getAuthorities(roleId, mappings);

        // 5. Spring Security의 UserDetails 객체로 변환하여 반환
        return new org.springframework.security.core.userdetails.User(
                user.getUserId(),
                user.getPassword(),
                user.isEnabled(),
                true, // account non expired
                true, // credentials non expired
                true, // account non locked
                authorities
        );
    }

    /**
     * 사용자에게 부여된 역할(Role)과 권한(Authority)을 Spring Security 권한 객체로 변환합니다.
     */
    private Collection<GrantedAuthority> getAuthorities(String roleId, List<RoleAuthMapping> mappings) {

        // 1. 매핑된 권한(Authority)을 추가
        List<GrantedAuthority> authorityList = mappings.stream()
                // Authority 엔티티에 getAuthority().getAuthority() 메서드가 있다고 가정
                .map(mapping -> mapping.getAuthority().getAuthority())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 2. 역할(Role) 자체를 권한으로 추가 (Role ID는 이미 "ROLE_" 접두사를 포함한다고 가정)
        authorityList.add(new SimpleGrantedAuthority(roleId));

        return authorityList;
    }

    // 🚨 buildMockUserDetails 헬퍼 메서드 제거
}