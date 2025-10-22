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

@Configuration
@EnableWebSecurity // Spring Security 활성화
@RequiredArgsConstructor // 모든 final 필드를 포함하는 생성자를 자동 생성
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    // ⭐️ 의존성 주입: JwtAuthenticationFilter를 Bean으로 등록하여 주입받습니다.
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    /**
     * 비밀번호 암호화에 사용할 PasswordEncoder Bean 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ⭐️ JwtAuthenticationFilter를 Bean으로 등록합니다. ⭐️
    // 이 필터는 JwtProvider에 의존하므로, RequiredArgsConstructor를 통해 주입받도록 구성합니다.
    /*
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        // JwtProvider는 이미 RequiredArgsConstructor를 통해 주입받고 있으므로,
        // 필터도 Bean으로 등록하거나, 아래 filterChain에서 직접 new로 생성하여 주입할 수 있습니다.
        // 현재는 필터를 @Component로 등록하고 주입받는 것이 더 일반적인 방식입니다.
        // 하지만 필터 클래스가 @Component가 아니므로, 여기서는 필터 객체를 직접 주입받아 사용하겠습니다.
        // 이를 위해 필터 필드에 final을 붙이고 RequiredArgsConstructor로 주입받도록 설정했습니다.
        return new JwtAuthenticationFilter(jwtProvider);
    }
    */


    /**
     * Spring Security 필터 체인 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                // 1. CSRF (Cross-Site Request Forgery) 보호 비활성화:
                .csrf(AbstractHttpConfigurer::disable)

                // 2. HTTP Basic 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)

                // 3. 세션 관리 비활성화: JWT를 사용하므로 STATELESS로 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. URL별 접근 권한 설정 (⭐️ 모든 /api/** 경로 허용 설정을 수정합니다. ⭐️)
                .authorizeHttpRequests(authorize -> authorize
                        // A. 인증 및 가입 관련 경로는 무조건 허용
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                        
                        // browse 관련 API는 로그인 없이 접근 허용
                        .requestMatchers("/api/products/**").permitAll()
                        .requestMatchers("/api/v1/pr/browse/**").permitAll()
                        .requestMatchers("/api/v1/pr/stores/**").permitAll()
                        .requestMatchers("/api/v1/pr/inventory/**").permitAll()
                        .requestMatchers("/api/po/**").permitAll()

                        
                        // B. ⭐️ 나머지 모든 /api/** 경로는 인증 필요! (토큰 검증) ⭐️
                        .requestMatchers("/api/**").authenticated()

                        // C. 위에 해당하지 않는 나머지 모든 요청도 기본적으로 인증 필요 (선택적)
                        .anyRequest().authenticated()
                )

                // 5. 예외 처리 핸들러 등록
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                );

        // 6. ⭐️ JWT 필터 등록 복구 ⭐️
        // UsernamePasswordAuthenticationFilter 이전에 JWT 필터를 추가하여 토큰 검증을 먼저 수행합니다.
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
