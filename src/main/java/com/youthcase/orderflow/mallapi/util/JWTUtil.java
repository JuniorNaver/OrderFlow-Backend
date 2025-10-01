package com.youthcase.orderflow.mallapi.util;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JWTUtil {
    // JWT를 서명하고 검증할 때 사용할 비밀키(Secret Key)
    // 길이는 HMAC-SHA 알고리즘에 따라 충분히 길어야 한다(256비트 이상).
    private static String key = "1234567890123456789012345678901234567890";

    /**
     * @param valueMap 토큰에 담을 정보(Claims). 보통 사용자의 아이디, 이메일, 역할 등을 포함.
     * @param min      토큰의 유효 기간(분 단위).
     * @return         생성된 JWT 문자열.
     */
    public static String generateToken(Map<String, Object> valueMap, int min) {
        // HMAC-SHA 알고리즘을 사용하여 문자열 키로부터 SecretKey 객체를 생성.
        // UTF-8로 인코딩된 바이트 배열을 사용
        SecretKey key = null;
        try {
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        String jwtStr = Jwts.builder()
                .setHeader(Map.of("typ", "JWT"))
                // Payload(Claims) 설정: 토큰에 담을 데이터를 설정.
                .setClaims(valueMap)
                // Payload(Claims) 설정: 토큰 발급 시간(Issued At)을 현재 시간으로 설정
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                // Payload(Claims) 설정: 토큰 만료 시간(Expiration)을 현재 시간으로부터 min분 후로 설정
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))
                // Signature 설정
                .signWith(key)
                // 최종적으로 JWT를 압축하여 문자열 형태
                .compact();

        return jwtStr;
    }

    /*
     * @param token 검증할 JWT 문자열.
     * @return 토큰에 담겨있던 Claims (Map 형태).
     * @throws CustomJWTException 토큰이 유효하지 않을 경우 (서명 오류, 만료, 형식 오류 등) 발생
     */
    public static Map<String, Object> validateToken(String token) {
        Map<String, Object> claim = null;

        try {
            SecretKey key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));

            //  JWT 파서(parser)를 사용하여 토큰을 파싱하고 검증.
            // setSigningKey(key)를 통해 서명이 올바른지 확인.
            // parseClaimsJws(token)은 서명 검증에 실패하거나 토큰 형식이 잘못된 경우 예외를 발생
            claim = Jwts.parserBuilder()
                    .setSigningKey(key).build()
                    .parseClaimsJws(token).getBody();
        } catch (MalformedJwtException malformedJwtException) {
            // JWT 문자열의 형식이 잘못되었을 때 발생하는 예외
            throw new CustomJWTException("MalFormed");
        } catch (ExpiredJwtException expiredJwtException) {
            // 토큰의 유효 기간이 만료되었을 때 발생하는 예외
            throw new CustomJWTException("Expired");
        } catch (InvalidClaimException invalidClaimException) {
            // 토큰의 Claim이 유효하지 않을 때 발생하는 예외
            throw new CustomJWTException("Invalid");
        } catch (JwtException jwtException) {
            // 그 외 JWT 관련 처리 중 발생하는 모든 예외
            throw new CustomJWTException("JWTError");
        } catch (Exception e) {
            throw new CustomJWTException("Error");
        }

        return claim;
    }
}