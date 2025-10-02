package com.youthcase.orderflow.auth.service.impl;

import com.youthcase.orderflow.auth.domain.PasswordResetToken;
import com.youthcase.orderflow.auth.dto.TokenResponseDTO;
import com.youthcase.orderflow.auth.provider.JwtProvider;
import com.youthcase.orderflow.auth.service.AuthService;
import com.youthcase.orderflow.auth.domain.User; // User 엔티티 추가
import com.youthcase.orderflow.auth.repository.UserRepository; // UserRepository 추가
import com.youthcase.orderflow.global.email.EmailService; // 이메일 발송 서비스 (가정)
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

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    private final UserRepository userRepository; // ⭐ 사용자 조회용 필드 추가
    private final EmailService emailService;     // ⭐ 이메일 발송용 필드 추가 (해당 서비스가 존재한다고 가정)

    @Override
    @Transactional
    public TokenResponseDTO authenticateAndGenerateToken(String userId, String password) {

        // 1. 인증 객체 생성 및 인증 시도 (기존 로그인 로직 유지)
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userId, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 2. 토큰 생성 후 반환
        TokenResponseDTO tokenResponse = jwtProvider.generateToken(authentication);

        return tokenResponse;
    }

    // ======================================================================
    // ⭐ 비밀번호 초기화 요청 로직 추가
    // ======================================================================

    /**
     * 비밀번호 초기화 요청을 처리하고, 초기화 토큰을 생성하여 사용자 이메일로 발송합니다.
     */
    @Override
    @Transactional
    public void requestPasswordReset(String userId) {

        // 1. 사용자 ID로 사용자 조회
        // GlobalExceptionHandler에서 처리되도록 IllegalArgumentException을 사용합니다.
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 ID를 찾을 수 없습니다."));

        // 2. 초기화 토큰 생성 (UUID 사용)
        String resetToken = generateUniqueResetToken();

        // ⭐ 중요: 토큰을 DB에 저장하는 로직이 여기에 추가되어야 합니다.
        // 이 토큰은 만료 시간과 함께 저장되어야 하며, 재설정 시 사용됩니다.
        // (예: passwordResetTokenRepository.save(...))

        // 3. 이메일 본문 생성 및 발송
        String resetLink = "https://yourdomain.com/reset-password?token=" + resetToken;
        String emailContent = buildResetEmailContent(user.getUserId(), resetLink);

        // 사용자의 이메일 주소로 초기화 링크 발송
        // (User 엔티티에 getEmail() 메서드가 있다고 가정합니다.)
        emailService.sendEmail(user.getEmail(), "[OrderFlow] 비밀번호 초기화 요청", emailContent);
    }

    // 초기화 토큰 생성 헬퍼 메서드
    private String generateUniqueResetToken() {
        return java.util.UUID.randomUUID().toString();
    }

    // 이메일 본문 생성 헬퍼 메서드
    private String buildResetEmailContent(String userId, String resetLink) {
        return "안녕하세요, " + userId + "님.\n\n" +
                "비밀번호를 초기화하려면 다음 링크를 클릭하세요: " + resetLink + "\n\n" +
                "이 링크는 보안을 위해 1시간 후 만료됩니다.";
    }

    @Override
    public String validatePasswordResetToken(String token) {

        // 1. 토큰 값으로 엔티티 조회
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않거나 만료된 토큰입니다."));

        // 2. 토큰 사용 여부 및 만료 시간 확인
        if (resetToken.isUsed() || resetToken.isExpired()) {
            // 이미 사용되었거나 만료된 경우
            throw new IllegalArgumentException("이미 사용되었거나 만료된 토큰입니다.");
        }

        // 3. 검증 성공 시 사용자 ID 반환
        return resetToken.getUserId();
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {

        // 1. 토큰 유효성 검사 및 사용자 ID 획득
        String userId = validatePasswordResetToken(token);

        // 2. 비밀번호 초기화에 사용된 토큰 사용 처리 (중복 사용 방지)
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다.")); // 이미 검증했지만 안전장치

        resetToken.useToken();
        passwordResetTokenRepository.save(resetToken); // 사용 플래그 업데이트

        // 3. 사용자 엔티티 조회 및 비밀번호 업데이트
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        // 새 비밀번호 암호화 및 업데이트
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword); // User 엔티티에 setPassword() 메서드가 있어야 함

        userRepository.save(user);
    }
}