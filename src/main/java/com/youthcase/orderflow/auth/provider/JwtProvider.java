package com.youthcase.orderflow.auth.provider;

import com.youthcase.orderflow.auth.dto.TokenResponseDTO;
import com.youthcase.orderflow.auth.service.security.CustomUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
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
 * JWT 토큰의 생성, 인증 정보 추출, 유효성 검사 등을 담당하는 핵심 컴포넌트입니다.
 */
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final CustomUserDetailsService customUserDetailsService;

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";

    private final long ACCESS_TOKEN_EXPIRE_TIME;
    private final long REFRESH_TOKEN_EXPIRE_TIME;
    private final Key key;

    // JWT Secret Key와 만료 시간 설정값을 주입받아 초기화합니다.
    public JwtProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expire-time}") long accessTime,
            @Value("${jwt.refresh-token-expire-time}") long refreshTime,
            CustomUserDetailsService customUserDetailsService) {

        // Secret Key를 Base64 Decode하여 Key 객체로 변환하고 HmacShaKeyFor로 서명 키 생성
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.ACCESS_TOKEN_EXPIRE_TIME = accessTime;
        this.REFRESH_TOKEN_EXPIRE_TIME = refreshTime;
        this.customUserDetailsService = customUserDetailsService;
    }


    /**
     * 1. Authentication 객체를 기반으로 Access Token과 Refresh Token을 생성합니다.
     * (최초 로그인 및 토큰 재발급 시 모두 사용됩니다.)
     */
    public TokenResponseDTO generateToken(Authentication authentication) {

        // 권한 목록을 "ROLE_ADMIN,STK_WRITE,ORDER_READ" 형태의 문자열로 변환
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())          // Subject: 사용자 ID (Principal)
                .claim(AUTHORITIES_KEY, authorities)           // 클레임: 권한 목록
                .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME)) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS512)       // 서명 (Signature)
                .compact();

        // Refresh Token 생성 (클레임 없이 만료 시간만 설정)
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME)) // 만료 시간
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
     * 2. 토큰에서 사용자 인증 정보를 추출하여 Authentication 객체를 반환합니다.
     */
    public Authentication getAuthentication(String accessToken) {
        // 1. 토큰 파싱하여 클레임 획득
        Claims claims = parseClaims(accessToken);

        // 2. 클레임에서 권한 정보 획득
        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 권한 문자열을 SimpleGrantedAuthority 객체 리스트로 변환
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 3. UserDetails 객체를 로드 (DB 재조회 필요, 또는 토큰 정보만으로 생성)
        // 여기서는 토큰의 subject(userId)를 사용하여 DB에서 최신 UserDetails를 로드합니다.
        UserDetails principal = customUserDetailsService.loadUserByUsername(claims.getSubject());

        // 4. 인증 객체 (Authentication) 반환
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 3. 토큰의 유효성을 검사합니다.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            // 유효하지 않은 JWT 서명 (디버깅을 위해 콘솔 출력)
            System.err.println("Invalid JWT signature: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            // 만료된 JWT 토큰
            System.err.println("Expired JWT token: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 JWT 토큰
            System.err.println("Unsupported JWT token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // JWT 클레임 문자열이 비어 있음
            System.err.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }

    /**
     * 토큰 파싱을 위한 내부 메서드: 만료된 토큰이라도 클레임은 반환합니다.
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
     * 토큰에서 사용자 ID(Subject)를 추출합니다.
     */
    public String getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }
}
