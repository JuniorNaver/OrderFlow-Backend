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

        // 1. Request Header에서 토큰 추출
        String jwt = resolveToken(request);

        // 2. 추출된 토큰의 유효성 검증
        if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {

            // 3. 토큰이 유효하면 인증 객체(Authentication) 생성
            Authentication authentication = jwtProvider.getAuthentication(jwt);

            // 4. SecurityContext에 인증 정보 저장
            // 이로써 해당 요청은 인증된 상태로 간주되어 컨트롤러까지 진행됩니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터로 진행
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
