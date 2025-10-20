package com.youthcase.orderflow.auth.filter;

import com.youthcase.orderflow.auth.provider.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 토큰을 검증하고 Security Context에 인증 정보를 설정하는 커스텀 필터입니다.
 * SecurityConfig에서 UsernamePasswordAuthenticationFilter 이전에 등록됩니다.
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    // HTTP 요청 헤더에서 JWT 토큰을 추출하는 데 사용되는 접두사
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    /**
     * 실제 필터링 로직을 수행합니다.
     * 모든 요청에서 토큰 정보를 검사하고 인증 객체를 SecurityContext에 저장합니다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = resolveToken(request);

        if (StringUtils.hasText(jwt)) {
            if (jwtProvider.validateToken(jwt)) {
                // 3. 토큰이 유효하면 인증 객체(Authentication) 생성 및 SecurityContext에 저장
                Authentication authentication = jwtProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // 💡 [변경] 유효하지 않은 토큰에 대해 Custom AuthenticationException을 던지도록 처리
                //    (Spring Security의 Exception Translation Filter가 이를 잡아 EntryPoint로 전달함)
                request.setAttribute("jwt_exception", "Invalid or Expired JWT Token");

                // 예외를 던지거나, EntryPoint를 명시적으로 호출해야 하지만,
                // 현재 로직을 유지하면서 Custom EntryPoint를 통해 401을 명확히 반환하는 것이 일반적입니다.
            }
        }

        // 다음 필터로 진행 (401 처리는 Spring Security의 기본 EntryPoint나 Custom EntryPoint에 위임됨)
        filterChain.doFilter(request, response);
    }

    /**
     * Request Header에서 토큰 정보를 꺼내오는 메서드
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        // "Bearer " 접두사가 붙어있는지 확인하고, 토큰 값만 반환
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
