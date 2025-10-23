package com.youthcase.orderflow.auth.service.security;

import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.RoleAuthMapping;
import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.repository.RoleAuthMappingRepository;
import com.youthcase.orderflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("customUserDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleAuthMappingRepository roleAuthMappingRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        // ✅ 단일 Role 기반 구조
        Role role = user.getRole();
        if (role == null) {
            throw new UsernameNotFoundException("사용자에게 역할(Role)이 지정되어 있지 않습니다: " + userId);
        }

        // 1️⃣ 권한(Authority) 목록 생성
        Set<GrantedAuthority> authorities = new HashSet<>();

        // 1-1. RoleAuthMapping에서 이 Role에 연결된 Authority를 모두 조회
        List<RoleAuthMapping> mappings = roleAuthMappingRepository.findByRole(role);
        if (mappings != null && !mappings.isEmpty()) {
            authorities.addAll(
                    mappings.stream()
                            .map(mapping -> new SimpleGrantedAuthority(mapping.getAuthority().getAuthority()))
                            .collect(Collectors.toSet())
            );
        }

        // 1-2. Role 자체도 ROLE_XXX 형태로 추가 (Spring Security 표준)
        authorities.add(new SimpleGrantedAuthority(role.getRoleId()));

        // 2️⃣ UserDetails 생성 및 반환
        return new org.springframework.security.core.userdetails.User(
                user.getUserId(),
                user.getPassword(),
                user.isEnabled(),
                true,  // accountNonExpired
                true,  // credentialsNonExpired
                true,  // accountNonLocked
                authorities
        );
    }
}
