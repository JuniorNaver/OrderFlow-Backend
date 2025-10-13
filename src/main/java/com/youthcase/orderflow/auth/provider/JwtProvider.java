package com.youthcase.orderflow.auth.provider;

import com.youthcase.orderflow.auth.dto.TokenResponseDTO;
import com.youthcase.orderflow.auth.service.security.CustomUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * ✅ JWT 토큰의 생성, 인증 정보 추출, 유효성 검사 등을 담당하는 핵심 컴포넌트입니다.
 * Spring Bean으로 관리되며, 인증 관련 모든 로직의 중심 역할을 합니다.
 */
@Slf4j
@Component
public class JwtProvider {

    // === 상수 정의 ===
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";

    // === 주입 필드 ===
    private final CustomUserDetailsService customUserDetailsService;
    private final long ACCESS_TOKEN_EXPIRE_TIME;
    private final long REFRESH_TOKEN_EXPIRE_TIME;
    private final Key key;

    /**
     * ✅ 명시적 생성자 기반 의존성 주입
     * Spring이 @Value와 CustomUserDetailsService를 모두 주입할 수 있도록 구성합니다.
     */
    public JwtProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expire-time}") long accessTime,
            @Value("${jwt.refresh-token-expire-time}") long refreshTime,
            CustomUserDetailsService customUserDetailsService
    ) {
        this.customUserDetailsService = customUserDetailsService;
        this.ACCESS_TOKEN_EXPIRE_TIME = accessTime;
        this.REFRESH_TOKEN_EXPIRE_TIME = refreshTime;

        // Secret Key를 Base64 Decode → Key 객체 생성
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * ✅ 1. Authentication 객체를 기반으로 Access/Refresh Token을 생성합니다.
     * 최초 로그인 및 토큰 재발급 시 사용됩니다.
     */
    public TokenResponseDTO generateToken(Authentication authentication) {

        // 권한 목록을 "ROLE_ADMIN,STK_WRITE,ORDER_READ" 형태의 문자열로 변환
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName()) // Subject: 사용자 ID (Principal)
                .claim(AUTHORITIES_KEY, authorities)  // 클레임: 권한 목록
                .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME)) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS512) // 서명
                .compact();

        // Refresh Token 생성 (클레임 없이 만료 시간만 설정)
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenResponseDTO.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(ACCESS_TOKEN_EXPIRE_TIME)
                .build();
    }

    /**
     * ✅ 2. 토큰에서 사용자 인증 정보를 추출하여 Authentication 객체를 반환합니다.
     */
    public Authentication getAuthentication(String accessToken) {
        // 1️⃣ 토큰 파싱하여 클레임 획득
        Claims claims = parseClaims(accessToken);

        // 2️⃣ 클레임에서 권한 정보 획득
        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 권한 문자열을 SimpleGrantedAuthority 객체 리스트로 변환
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 3️⃣ 토큰의 subject(userId)를 사용하여 DB에서 최신 UserDetails를 로드
        UserDetails principal = customUserDetailsService.loadUserByUsername(claims.getSubject());

        // 4️⃣ 인증 객체(Authentication) 반환
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * ✅ 3. 토큰의 유효성을 검사합니다.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.warn("❌ Invalid JWT signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("⚠️ Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("❌ Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("❌ JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * ✅ 토큰 파싱을 위한 내부 메서드
     * 만료된 토큰이라도 클레임은 반환합니다.
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰의 경우에도 클레임(사용자 ID 등)을 추출하기 위해 반환
            return e.getClaims();
        }
    }

    /**
     * ✅ 토큰에서 사용자 ID(Subject)를 추출합니다.
     */
    public String getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }
}
