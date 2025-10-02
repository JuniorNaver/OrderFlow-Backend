package com.youthcase.orderflow.auth.filter;

import com.youthcase.orderflow.auth.provider.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    // 토큰 타입 접두사
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    /**
     * HTTP 요청이 들어올 때마다 실행되어 토큰을 검증하고 인증 정보를 설정합니다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. HTTP 헤더에서 JWT 토큰 추출
        String jwt = resolveToken(request);

        // 2. 추출된 토큰의 유효성 검사 및 인증 처리
        if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {

            // 토큰이 유효하면, 토큰에서 사용자 인증 정보를 가져옵니다.
            Authentication authentication = jwtProvider.getAuthentication(jwt);

            // SecurityContext에 인증 정보를 설정하여 현재 요청이 인증된 상태임을 알립니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터 또는 서블릿으로 요청을 전달
        filterChain.doFilter(request, response);
    }

    /**
     * Request Header에서 토큰 정보 추출 (Bearer 접두사 제거)
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            // "Bearer " 접두사 이후의 실제 토큰 부분만 반환
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}