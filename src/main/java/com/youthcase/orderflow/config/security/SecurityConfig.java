package com.youthcase.orderflow.config.security;

import com.youthcase.orderflow.auth.filter.JwtAuthenticationFilter; // 새로 추가
import com.youthcase.orderflow.auth.handler.JwtAccessDeniedHandler;
import com.youthcase.orderflow.auth.handler.JwtAuthenticationEntryPoint;
import com.youthcase.orderflow.auth.service.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager; // 추가
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // 추가
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy; // 추가: JWT는 세션을 사용하지 않음
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // 추가: 필터 삽입 위치 지정

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // ⭐ 새로 추가: JWT 필터 주입

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;


    // --- 1. 필수 Bean 등록 (기존 코드 유지) ---

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "ROLE_ADMIN > ROLE_MANAGER \n ROLE_MANAGER > ROLE_CLERK";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // ⭐ 새로 추가: AuthService에서 인증에 사용할 AuthenticationManager를 Bean으로 노출
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // --- 2. HTTP 보안 규칙 설정 (JWT 필터 통합) ---

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {



        http
                // JWT 사용 시 CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // JWT 사용 시 세션을 사용하지 않음 (STATELESS)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 폼 로그인 및 HTTP Basic 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 요청 접근 규칙 설정
                .authorizeHttpRequests(authorize -> authorize
                        // 로그인, 회원가입 API는 인증 없이 접근 허용
                        .requestMatchers("/api/auth/login", "/api/auth/users/register").permitAll()

                        // 관리자 API는 ADMIN 역할만 접근 허용
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 그 외 모든 요청은 인증된 사용자에게만 허용
                        .anyRequest().authenticated()
                )

                // ⭐ 예외 처리 설정 추가: 인증/권한 오류 발생 시 두 핸들러를 사용
                .exceptionHandling(exception -> exception
                        // 인증되지 않은 사용자 접근 (401 Unauthorized)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        // 권한이 없는 사용자 접근 (403 Forbidden)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                // ⭐ 새로 추가: UsernamePasswordAuthenticationFilter 이전에 JWT 필터를 추가하여 토큰 검증
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}