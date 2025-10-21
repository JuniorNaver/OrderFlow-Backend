package com.youthcase.orderflow.auth.service.security;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.RoleAuthMapping;
// 💡 UserRole 엔티티가 있다면 import 되어야 합니다.
import com.youthcase.orderflow.auth.domain.UserRole;
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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;

@Service("customUserDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    // 💡 RoleAuthMappingRepository는 UserRole을 통해 Role을 가져온 후 사용할 수도 있고,
    // 로직을 간소화하기 위해 Role 엔티티 내부에 Authority 정보를 EAGER 로딩하여 사용할 수도 있습니다.
    private final RoleAuthMappingRepository roleAuthMappingRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        User user = userRepository.findByUserId(userId) // <- 이 메서드가 이제 EntityGraph로 최적화됨
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        // 1. UserRole 매핑을 통해 사용자가 가진 모든 Role 엔티티를 가져옵니다.
        Set<Role> roles = user.getUserRoles().stream()
                .map(UserRole::getRole) // 💡 UserRole 엔티티에서 Role 엔티티를 추출
                .collect(Collectors.toSet());

        if (roles.isEmpty()) {
            throw new UsernameNotFoundException("사용자에게 할당된 역할(Role)이 없습니다.");
        }

        // 2. 모든 Role에서 연결된 Authority 목록을 가져와 통합합니다.
        Set<GrantedAuthority> authorities = new HashSet<>();

        // 2-1. 각 Role에 부여된 모든 Authority를 GrantedAuthority로 변환하여 추가
        for (Role role : roles) {
            // RoleAuthMappingRepository를 사용하는 대신, Role 엔티티의 Lazy 로딩을 피하기 위해
            // 미리 EAGER 페치를 설정했거나, 아래처럼 Stream을 사용한다고 가정합니다.

            // 💡 RoleAuthMapping 컬렉션을 통해 Authority를 추출
            Set<GrantedAuthority> roleAuthorities = role.getRoleAuthMappings().stream()
                    .map(RoleAuthMapping::getAuthority)
                    .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                    .collect(Collectors.toSet());

            authorities.addAll(roleAuthorities);

            // 2-2. Role ID 자체도 Spring Security의 권한(Authority)으로 추가 (ROLE_ADMIN 등)
            authorities.add(new SimpleGrantedAuthority(role.getRoleId()));
        }

        // 3. Spring Security의 UserDetails 객체로 변환하여 반환
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
     * 💡 (삭제됨): 이전에 사용하던 getAuthorities 헬퍼 메서드는 위 통합 로직으로 대체되었습니다.
     */
}