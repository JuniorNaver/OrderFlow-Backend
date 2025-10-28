package com.youthcase.orderflow.global.config.security;

import com.youthcase.orderflow.auth.filter.JwtAuthenticationFilter;
import com.youthcase.orderflow.auth.handler.JwtAccessDeniedHandler;
import com.youthcase.orderflow.auth.handler.JwtAuthenticationEntryPoint;
import com.youthcase.orderflow.auth.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * ✅ Spring Security 전역 설정
 * - JWT 인증 구조 기반
 * - 세션 X (STATELESS)
 * - CSRF / Form 로그인 비활성화
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * ✅ 비밀번호 암호화용 PasswordEncoder 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * ✅ Spring Security 필터 체인 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS 설정
                .cors(Customizer.withDefaults())

                // CSRF / FormLogin / HTTP Basic 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // JWT 기반 → 세션 사용하지 않음
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 요청별 인가 설정
                .authorizeHttpRequests(auth -> auth
                        // ✅ 인증 불필요 (화이트리스트)
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/products/**",
                                "/api/v1/pr/browse/**",
                                "/api/v1/pr/stores/**",
                                "/api/v1/pr/inventory/**",
                                "/api/po/**",
                                "/api/gr/**",
                                "/api/sd/**",
                                "/api/payments/**",
                                "/api/receipts/**",
                                "/api/refunds/**",
                                "/api/stk/**",
                                "/api/auth/reissue", // ⭐️ 추가
                                "/api/auth/password/**" // ⭐️ 추가: 비밀번호 초기화 관련 전체 경로 허용
                        ).permitAll()

                        // ✅ 나머지 /api/** 경로는 인증 필요
                        .requestMatchers("/api/**").authenticated()

                        // ✅ 나머지 요청은 모두 허용 (필요 시 authenticated로 변경)
                        .anyRequest().permitAll()
                )

                // 예외 처리 핸들러 등록
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                );

        // ✅ JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 등록
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * ✅ AuthenticationManager Bean 등록
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * ✅ CORS 정책 (선택적)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // 프론트 도메인으로 제한 가능
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
