package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.domain.PasswordResetToken;
import com.youthcase.orderflow.auth.domain.RefreshToken;
import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.dto.LoginRequestDTO;
import com.youthcase.orderflow.auth.dto.TokenResponseDTO;
import com.youthcase.orderflow.auth.dto.UserRegisterRequestDTO;
import com.youthcase.orderflow.auth.exception.DuplicateUserException;
import com.youthcase.orderflow.auth.provider.JwtProvider;
import com.youthcase.orderflow.auth.repository.PasswordResetTokenRepository;
import com.youthcase.orderflow.auth.repository.RefreshTokenRepository;
import com.youthcase.orderflow.auth.repository.UserRepository;
import com.youthcase.orderflow.auth.service.security.CustomUserDetailsService;
import com.youthcase.orderflow.global.exception.AuthException;
import com.youthcase.orderflow.global.exception.ErrorCode;
import com.youthcase.orderflow.global.util.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier; // Qualifier import
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional(readOnly = true)
// 🚨 Bean 충돌 해결을 위해 @RequiredArgsConstructor 대신 명시적 생성자 사용
public class AuthServiceImpl implements AuthService {

    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // 🚨 명시적 생성자 주입을 통해 EmailService Bean을 확정합니다.
    public AuthServiceImpl(
            CustomUserDetailsService customUserDetailsService,
            RefreshTokenRepository refreshTokenRepository,
            JwtProvider jwtProvider,
            PasswordResetTokenRepository passwordResetTokenRepository,
            UserRepository userRepository,
            @Qualifier("mockEmailService") EmailService emailService, // @Qualifier 적용
            PasswordEncoder passwordEncoder
    ) {
        this.customUserDetailsService = customUserDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProvider = jwtProvider;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * 사용자 ID, 비밀번호, 워크스페이스를 인증하고, 성공 시 JWT 토큰을 생성하여 반환합니다.
     *
     * @param request 로그인 요청 DTO (userId, password, workspace 포함)
     * @return 발급된 Access Token 및 Refresh Token 정보를 담은 TokenResponseDTO
     */
    @Override
    @Transactional
    public TokenResponseDTO authenticateAndGenerateToken(LoginRequestDTO request) {

        // 1. userId와 workspace로 사용자 존재 여부 확인
        User user = userRepository.findByUserIdAndWorkspace(request.getUserId(), request.getWorkspace())
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND, "ID, 비밀번호 또는 워크스페이스 불일치"));

        // 2. 비밀번호 검증 (PasswordEncoder 직접 사용)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException(ErrorCode.BAD_CREDENTIALS, "ID, 비밀번호 또는 워크스페이스 불일치");
        }

        // 3. UserDetails 로드 및 인증 객체 생성
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getUserId());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null, // 비밀번호는 이미 검증되었으므로 null
                userDetails.getAuthorities()
        );

        // 4. 토큰 발급 및 Refresh Token 저장 (Rotation 또는 최초 저장)
        TokenResponseDTO tokenResponse = jwtProvider.generateToken(authentication);

        refreshTokenRepository.findByUserId(request.getUserId())
                .ifPresentOrElse(
                        // 이미 존재하면 토큰 값만 업데이트 (Rotation)
                        entity -> entity.updateToken(tokenResponse.getRefreshToken()),
                        // 존재하지 않으면 새로 저장
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .userId(request.getUserId())
                                .token(tokenResponse.getRefreshToken())
                                .build())
                );

        log.info("로그인 성공: User ID: {}, Workspace: {}", request.getUserId(), request.getWorkspace());

        return tokenResponse;
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {

        // 1. 토큰 유효성 검사 및 사용자 ID 획득
        String userId = validatePasswordResetToken(token);

        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new AuthException(ErrorCode.INVALID_TOKEN, String.format("유효한 토큰을 찾을 수 없습니다: %s", token)));
        resetToken.useToken();
        passwordResetTokenRepository.save(resetToken); // 사용 플래그 업데이트

        // 3. 사용자 엔티티 조회 및 비밀번호 업데이트
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        // 새 비밀번호 암호화 및 업데이트
        String encodedPassword = passwordEncoder.encode(newPassword);

        // User 엔티티에 updatePassword 메서드가 있다고 가정
        user.updatePassword(encodedPassword);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public TokenResponseDTO reissueToken(String refreshToken) {

        // 1. Refresh Token의 유효성 검사 (JwtProvider에서 만료 여부, 형식 등을 검사)
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new AuthException(ErrorCode.INVALID_REFRESH_TOKEN, "유효하지 않거나 만료된 Refresh Token입니다. 재로그인이 필요합니다.");
        }

        // 2. DB에서 Refresh Token 정보 조회 및 사용자 ID 획득
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AuthException(ErrorCode.INVALID_REFRESH_TOKEN, "DB에서 유효한 Refresh Token 정보를 찾을 수 없습니다."));

        String userId = refreshTokenEntity.getUserId();

        // 3. 사용자 ID로 UserDetails 로드 및 권한 정보 획득
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null, // 비밀번호는 이미 검증되었으므로 null
                userDetails.getAuthorities()
        );

        // 4. 새로운 Access Token 및 Refresh Token 생성
        TokenResponseDTO newTokens = jwtProvider.generateToken(newAuthentication);

        // 5. Refresh Token Rotation (DB의 Refresh Token을 새로 발급된 값으로 업데이트)
        refreshTokenEntity.updateToken(newTokens.getRefreshToken());
        refreshTokenRepository.save(refreshTokenEntity);

        return newTokens;
    }

    /**
     * 비밀번호 초기화 요청을 처리하고, 초기화 토큰을 생성하여 사용자 이메일로 발송합니다.
     */
    @Override
    @Transactional
    public void requestPasswordReset(String userId) {

        // 1. 사용자 ID로 사용자 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND, "사용자 ID를 찾을 수 없습니다."));

        // 2. 초기화 토큰 생성 (UUID 사용)
        String resetToken = generateUniqueResetToken();

        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);

        PasswordResetToken tokenEntity = PasswordResetToken.builder()
                .user(user)
                .token(resetToken)
                .expiryDate(expiryDate)
                .used(false)
                .build();

        passwordResetTokenRepository.save(tokenEntity);

        // 3. 이메일 본문 생성 및 발송
        String resetLink = "https://yourdomain.com/reset-password?token=" + resetToken;
        String emailContent = buildResetEmailContent(user.getUserId(), resetLink);

        emailService.sendEmail(user.getEmail(), "[OrderFlow] 비밀번호 초기화 요청", emailContent);
    }

    // 헬퍼 메서드: 초기화 토큰 생성
    private String generateUniqueResetToken() {
        return java.util.UUID.randomUUID().toString();
    }

    // 헬퍼 메서드: 이메일 본문 생성
    private String buildResetEmailContent(String userId, String resetLink) {
        return "안녕하세요, " + userId + "님.\n\n" +
                "비밀번호를 초기화하려면 다음 링크를 클릭하세요: " + resetLink + "\n\n" +
                "이 링크는 보안을 위해 1시간 후 만료됩니다.";
    }

    @Override
    public String validatePasswordResetToken(String token) {

        // 1. 토큰 값으로 엔티티 조회
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new AuthException(ErrorCode.INVALID_TOKEN, String.format("유효한 토큰을 찾을 수 없습니다: %s", token)));

        // 2. 토큰 사용 여부 및 만료 시간 확인
        if (resetToken.isUsed() || resetToken.isExpired()) {
            throw new AuthException(ErrorCode.INVALID_TOKEN, "이미 사용되었거나 만료된 토큰입니다.");
        }

        // 3. 검증 성공 시 사용자 ID 반환
        return resetToken.getUser().getUserId();
    }

    /**
     * 사용자 회원가입을 처리하고, 생성된 사용자의 ID를 반환합니다.
     */
    @Override
    @Transactional
    public String registerNewUser(UserRegisterRequestDTO request) {

        // 1. userId 중복 확인
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new DuplicateUserException("이미 존재하는 사용자 ID입니다: " + request.getUserId());
        }

        // 2. DTO 정보를 기반으로 User 엔티티 생성
        User user = User.builder()
                .userId(request.getUserId())
                .name(request.getUsername())
                .email(request.getEmail())
                .workspace(request.getWorkspace())
                // 3. 비밀번호 암호화
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        // 4. 저장 및 생성된 User ID 반환
        User savedUser = userRepository.save(user);

        return savedUser.getUserId();
    }
}
