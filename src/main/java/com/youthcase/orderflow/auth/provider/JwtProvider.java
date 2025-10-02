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

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final CustomUserDetailsService customUserDetailsService; // 인증 정보 로드를 위해 주입

    // application.yml/properties에서 주입받을 JWT 설정 값
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

        // Secret Key를 Base64 Decode하여 Key 객체로 변환
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.ACCESS_TOKEN_EXPIRE_TIME = accessTime;
        this.REFRESH_TOKEN_EXPIRE_TIME = refreshTime;
        this.customUserDetailsService = customUserDetailsService;
    }


    /**
     * 1. Authentication 객체를 기반으로 Access Token과 Refresh Token을 생성합니다.
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

        // Refresh Token 생성
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
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 3. UserDetails 객체를 로드 (DB 재조회 없이 토큰 정보만 사용 가능)
        // 여기서는 DB를 한 번 더 거치지 않고, 토큰의 subject(userId)만으로 UserDetails를 만들 수 있습니다.
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
            // 유효하지 않은 JWT 서명
        } catch (ExpiredJwtException e) {
            // 만료된 JWT 토큰
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 JWT 토큰
        } catch (IllegalArgumentException e) {
            // JWT 클레임 문자열이 비어 있음
        }
        return false;
    }

    /**
     * 토큰 파싱을 위한 내부 메서드
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰의 경우에도 클레임은 필요하므로 만료 예외에서 클레임을 반환합니다.
            return e.getClaims();
        }
    }
}