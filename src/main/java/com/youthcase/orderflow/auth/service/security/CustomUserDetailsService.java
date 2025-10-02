package com.youthcase.orderflow.auth.service.security; // security 하위 패키지 제안

import com.youthcase.orderflow.auth.domain.User;
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

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleAuthMappingRepository roleAuthMappingRepository;

    /**
     * 사용자 ID(userId)를 기반으로 사용자 인증 정보를 로드합니다.
     * Spring Security의 loadUserByUsername 메서드를 구현합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        // 1. 사용자 엔티티 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        // 2. 해당 사용자의 역할 ID로 부여된 모든 권한 매핑 정보를 조회
        List<RoleAuthMapping> mappings = roleAuthMappingRepository.findById_RoleId(user.getRoleId());

        // 3. 권한 매핑 정보와 역할 ID를 기반으로 Spring Security의 권한(Authorities) 리스트 생성
        Collection<GrantedAuthority> authorities = getAuthorities(user, mappings);

        // 4. Spring Security의 UserDetails 객체로 변환하여 반환
        return new org.springframework.security.core.userdetails.User(
                user.getUserId(), // 계정 ID
                user.getPassword(), // 암호화된 비밀번호
                authorities // 부여된 역할 및 권한 목록
        );
    }

    /**
     * 사용자에게 부여된 역할(Role)과 권한(Authority)을 Spring Security 권한 객체로 변환합니다.
     */
    private Collection<GrantedAuthority> getAuthorities(User user, List<RoleAuthMapping> mappings) {

        // 1. 역할(Role) 자체를 권한으로 추가 (Spring Security 규칙: "ROLE_" 접두사 사용)
        String roleAuthority = user.getRoleId();

        // 2. 매핑된 권한(Authority)을 추가
        List<GrantedAuthority> authorityList = mappings.stream()
                // RoleAuthMapping에서 Authority 엔티티를 추출하고, 그 안의 권한명(String)을 SimpleGrantedAuthority로 변환
                .map(mapping -> mapping.getAuthority().getAuthority())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 3. 역할과 권한을 모두 포함하여 반환
        authorityList.add(new SimpleGrantedAuthority(roleAuthority));

        return authorityList;
    }
}