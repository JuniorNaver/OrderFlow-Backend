package com.youthcase.orderflow.global.config.security;

import com.youthcase.orderflow.auth.filter.JwtAuthenticationFilter;
import com.youthcase.orderflow.auth.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Spring Security 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    /**
     * 비밀번호 암호화에 사용할 PasswordEncoder Bean 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt는 현재 가장 널리 사용되는 안전한 비밀번호 해시 함수입니다.
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security 필터 체인 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF (Cross-Site Request Forgery) 보호 비활성화:
                // REST API는 JWT를 사용하며 세션을 사용하지 않으므로 CSRF가 필요하지 않습니다.
                .csrf(AbstractHttpConfigurer::disable)

                // 2. HTTP Basic 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)

                // 3. 세션 관리 비활성화: JWT를 사용하므로 STATELESS로 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. URL별 접근 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        // 1. 누구나 접근 가능 (인증/재발급)
                        .requestMatchers("/api/auth/**").permitAll()

                        // 2. 역할 기반 접근 제어 (ROLE_ADMIN 권한이 필요)
                        // 🚨 User 엔티티의 RoleId 필드에 "ROLE_" 접두사를 사용했으므로 hasRole 대신 hasAuthority를 사용하거나,
                        //    CustomUserDetailsService에서 "ROLE_" 접두사를 붙여 SimpleGrantedAuthority로 변환했다면 hasRole 사용 가능
                        .requestMatchers("/api/products/**").hasAuthority("PRODUCT_ADMIN") // 특정 권한 필요
                        .requestMatchers("/api/orders/admin/**").hasRole("ADMIN") // RoleType의 ADMIN 역할 필요

                        // 3. 인증된 사용자만 (나머지)
                        .anyRequest().authenticated()
                )

                // 5. JWT 필터 등록
                // UsernamePasswordAuthenticationFilter 이전에 커스텀 JWT 필터를 삽입하여 토큰 검증을 수행합니다.
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class
                );



        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}