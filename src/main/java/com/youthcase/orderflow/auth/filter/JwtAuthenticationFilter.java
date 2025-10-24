package com.youthcase.orderflow.auth.filter;

import com.youthcase.orderflow.auth.provider.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * ✅ JWT 인증 필터
 * 인증이 필요하지 않은 경로는 필터를 건너뛰고,
 * 인증이 필요한 경로에서만 JWT 검증 수행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    /**
     * ✅ 인증이 필요 없는 경로(화이트리스트)
     */
    private static final List<String> NO_AUTH_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/products",
            "/api/products/",
            "/api/v1/pr/browse",
            "/api/v1/pr/stores",
            "/api/v1/pr/inventory",
            "/api/po",
            "/api/gr",
            "/api/sd",
            "/api/payments",
            "/api/receipts",
            "/api/refunds",
            "/api/stk"
    );

    /**
     * ✅ 화이트리스트 경로는 필터를 적용하지 않음
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return NO_AUTH_PATHS.stream().anyMatch(path::startsWith);
    }

    /**
     * ✅ JWT 검증 로직
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String token = resolveToken(request);

        if (token != null) {
            try {
                if (jwtProvider.validateToken(token)) {
                    Authentication authentication = jwtProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("✅ Authenticated user: {}", authentication.getName());
                }
            } catch (Exception e) {
                log.warn("⚠️ JWT validation failed for {}: {}", path, e.getMessage());
            }
        } else {
            log.trace("No JWT token found for path: {}", path);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * ✅ Request Header에서 Bearer 토큰 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
