package com.youthcase.orderflow.global.config.security;

import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * JWT 토큰 인증 후 SecurityContext에 저장될 커스텀 UserDetails 객체입니다.
 * JWT의 Subject(사용자 ID)와 권한 정보를 담습니다.
 */
@Getter
@ToString
public class SecurityUser implements UserDetails {

    // JWT Subject에서 추출한 사용자 고유 ID (Username으로 사용)
    private final String userId;

    // JWT 인증 시에는 사용하지 않으므로 빈 문자열을 받습니다.
    private final String password;

    // JWT Claims에서 추출한 권한 목록
    private final Collection<? extends GrantedAuthority> authorities;

    private final boolean isEnabled;

    /**
     * SecurityUser 생성자.
     * @param userId 토큰의 Subject (Username)
     * @param password JWT 인증 시에는 빈 문자열 ("")
     * @param authorities 사용자의 권한 목록
     */
    public SecurityUser(String userId, String password, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.password = password;
        this.authorities = authorities;
        this.isEnabled = true; // 기본적으로 계정 활성화
    }

    // --- UserDetails 인터페이스 구현 메서드 ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        // JWT 기반이므로 실제로 사용되지 않음
        return this.password;
    }

    @Override
    public String getUsername() {
        // Spring Security에서 식별자로 사용됨
        return this.userId;
    }

    // 모든 계정 관련 설정은 기본적으로 true로 설정
    @Override
    public boolean isAccountNonExpired() { return isEnabled; }

    @Override
    public boolean isAccountNonLocked() { return isEnabled; }

    @Override
    public boolean isCredentialsNonExpired() { return isEnabled; }

    @Override
    public boolean isEnabled() { return isEnabled; }
}
