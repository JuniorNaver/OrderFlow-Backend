package com.youthcase.orderflow.auth.provider;

import com.youthcase.orderflow.auth.dto.TokenResponseDTO;
import com.youthcase.orderflow.global.config.security.SecurityUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
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
 * JWT 토큰 생성, 유효성 검증 및 인증 객체(Authentication) 추출을 담당합니다.
 */
@Slf4j
@Component

public class JwtProvider {

    private final Key key;
    private final long accessTokenExpirationMillis;
    private final long refreshTokenExpirationMillis;

    private static final String BEARER_TYPE = "Bearer";

    // 생성자 (application.yml 값 주입 및 Key 초기화)
    public JwtProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expiration-millis}") long accessTokenExpirationMillis,
            @Value("${jwt.refresh-token-expiration-millis}") long refreshTokenExpirationMillis
    ) {
        // Key 초기화 로직은 그대로 유지
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpirationMillis = accessTokenExpirationMillis;
        this.refreshTokenExpirationMillis = refreshTokenExpirationMillis;
    }


    /**
     * [✅ AuthServiceImpl의 호출 오류 해결]
     * Authentication 객체를 기반으로 Access Token과 Refresh Token을 생성하고 DTO로 반환합니다.
     */
    public TokenResponseDTO generateToken(Authentication authentication) {

        // 1. 권한 정보 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String userId = authentication.getName(); // SecurityUser의 getUsername() (즉, userId)

        long now = (new Date()).getTime();

        // 2. Access Token 생성
        Date accessTokenExpiresIn = new Date(now + accessTokenExpirationMillis);

        String accessToken = Jwts.builder()
                .setSubject(userId)
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // 3. Refresh Token 생성
        // Refresh Token은 보통 권한 정보 없이 만료 시간만 설정합니다.
        String refreshToken = Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(now + refreshTokenExpirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // 4. TokenResponseDTO로 묶어서 반환
        return TokenResponseDTO.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                // Access Token 만료 시간 (밀리초 단위)을 DTO에 포함
                .accessTokenExpiresIn(accessTokenExpirationMillis)
                .build();
    }

    /**
     * JWT 토큰의 유효성을 검사하는 메서드. (이전에 추가된 로깅 유지)
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token structure: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * JWT에서 인증 정보(Authentication)를 추출하는 메서드.
     */
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("Authority information is missing in JWT");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new SecurityUser(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 토큰에서 Claims를 추출하는 내부 메서드.
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
