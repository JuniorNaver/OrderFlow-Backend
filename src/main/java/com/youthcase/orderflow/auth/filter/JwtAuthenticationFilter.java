package com.youthcase.orderflow.auth.filter;

import com.youthcase.orderflow.auth.provider.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component; // ⭐️ 추가됨 ⭐️
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 토큰을 검증하고 Security Context에 인증 정보를 설정하는 커스텀 필터입니다.
 * SecurityConfig에서 UsernamePasswordAuthenticationFilter 이전에 등록됩니다.
 */
@Component // ⭐️ Spring Bean으로 등록하여 SecurityConfig에서 주입받을 수 있게 함 ⭐️
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
                // 💡 유효하지 않은 토큰은 인증 객체를 제거하고, EntryPoint로 전달할 속성 설정
                SecurityContextHolder.clearContext();
                request.setAttribute("jwt_exception", "Invalid or Expired JWT Token");

                // ⭐️ [중요]: 유효하지 않은 토큰이지만 permitAll()이 아니기 때문에
                // Spring Security가 401(AuthenticationEntryPoint)로 이동시킵니다.
            }
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
