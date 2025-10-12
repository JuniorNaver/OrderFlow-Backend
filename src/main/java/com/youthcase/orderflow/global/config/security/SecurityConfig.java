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
                .csrf(AbstractHttpConfigurer::disable)

                // 2. HTTP Basic 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)

                // 3. 세션 관리 비활성화: JWT를 사용하므로 STATELESS로 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. URL별 접근 권한 설정 (최신 Spring Security 6+ 문법 적용)
                .authorizeHttpRequests(authorize -> authorize
                                // ----------------------------------------------------
                                // [개발/테스트를 위해 수정된 핵심 부분]
                                // 모든 API 경로 (/api/**)에 대해 인증 없이 접근을 허용합니다. (개발용)
                                .requestMatchers("/api/**").permitAll()

                                // 나머지 모든 요청 (정적 파일 등)도 모두 허용합니다.
                                .anyRequest().permitAll()

                        // ----------------------------------------------------
                        // ✨ 원래의 엄격한 설정은 아래와 같았습니다.
                        /*
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/products/**").hasAuthority("PRODUCT_ADMIN")
                        .requestMatchers("/api/orders/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                        */
                )

                // 5. JWT 필터 등록
                // 인증을 해제했으므로 토큰 검증은 진행되지만, 실패해도 접근은 허용됩니다.
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