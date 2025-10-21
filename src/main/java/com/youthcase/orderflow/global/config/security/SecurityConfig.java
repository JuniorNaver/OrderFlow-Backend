package com.youthcase.orderflow.global.config.security;

import com.youthcase.orderflow.auth.filter.JwtAuthenticationFilter;
import com.youthcase.orderflow.auth.provider.JwtProvider;
import com.youthcase.orderflow.auth.handler.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security 필터 체인 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. URL별 접근 권한 설정 (인증 정책 활성화)
                .authorizeHttpRequests(authorize -> authorize
                        // 인증/재발급/회원가입/비밀번호 찾기 등은 인증 없이 허용
                        .requestMatchers("/api/auth/login", "/api/auth/reissue", "/api/auth/register", "/api/auth/password/**").permitAll()

                        // /api/auth/users/me는 인증 필요
                        .requestMatchers("/api/auth/users/me").authenticated()

                        // 💡 수정: 게시판, 메인, 공통, BI 경로를 인증된 사용자에게 허용
                        // Home.jsx가 호출하는 BI API 접근을 허용합니다.
                        .requestMatchers("/board/**", "/api/dashboard", "/api/main/**", "/api/common/**", "/api/bi/**").authenticated()

                        // 관리자 경로는 ADMIN 역할 필요
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 예시: 상품 목록 조회는 모두 허용, 등록/수정/삭제는 권한 필요
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAnyRole("ADMIN", "MANAGER")

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // 5. 예외 처리 설정
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                )

                // 6. JWT 필터 등록
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
