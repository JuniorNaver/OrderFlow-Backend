package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.dto.TokenResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager; // SecurityConfig에서 Bean으로 등록해야 함
    private final JwtProvider jwtProvider; // 토큰 생성 로직을 위임

    @Override
    @Transactional // 토큰 저장 로직 등이 있다면 쓰기 트랜잭션 필요
    public TokenResponseDTO authenticateAndGenerateToken(String userId, String password) {

        // 1. 인증 객체 생성 (ID와 PW)
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userId, password);

        // 2. AuthenticationManager를 통해 인증 시도 (CustomUserDetailsService 호출)
        // 실패 시 AuthenticationException 발생 (Controller Advice에서 처리 필요)
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 3. 인증 성공 시, JwtProvider를 사용하여 토큰 생성 및 반환
        TokenResponseDTO tokenResponse = jwtProvider.generateToken(authentication);

        // ⭐ 참고: 일반적으로 Refresh Token을 DB나 Redis에 저장하는 로직이 이 위치에 추가됩니다.

        return tokenResponse;
    }
}