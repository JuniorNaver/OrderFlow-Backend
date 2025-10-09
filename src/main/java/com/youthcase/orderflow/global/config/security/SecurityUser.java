package com.youthcase.orderflow.global.config.security; // 🚨 패키지 경로 수정

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.domain.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class SecurityUser implements UserDetails {

    private final User user;

    public SecurityUser(User user) {
        this.user = user;
    }

    /**
     * 사용자가 가진 권한 목록을 반환합니다. (에러 35 해결)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 지역 변수명 변경 (충돌 방지)
        Set<SimpleGrantedAuthority> grantedAuthorities = user.getRoles().stream() // User 엔티티의 getRoles() 호출 가정
                .map(Role::getRoleId)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return grantedAuthorities; // 수정된 변수 반환
    }

    // --- UserDetails 필수 메서드 구현 ---

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserId();
    }

    // --- 계정 상태 관련 메서드 ---

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled(); // User 엔티티의 isEnabled() 호출 가정 (에러 81 해결)
    }
}