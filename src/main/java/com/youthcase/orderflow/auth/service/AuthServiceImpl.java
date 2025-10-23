package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.domain.PasswordResetToken;
import com.youthcase.orderflow.auth.domain.RefreshToken;
import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.dto.TokenResponseDTO;
import com.youthcase.orderflow.auth.dto.UserRegisterRequestDTO;
import com.youthcase.orderflow.auth.exception.DuplicateUserException;
import com.youthcase.orderflow.auth.provider.JwtProvider;
import com.youthcase.orderflow.auth.repository.PasswordResetTokenRepository;
import com.youthcase.orderflow.auth.repository.RefreshTokenRepository;
import com.youthcase.orderflow.auth.repository.RoleRepository;
import com.youthcase.orderflow.auth.repository.UserRepository;
import com.youthcase.orderflow.auth.service.security.CustomUserDetailsService;
import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StoreRepository storeRepository;

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public TokenResponseDTO authenticateAndGenerateToken(String userId, String password) {

        // 1. 인증 객체 생성 및 인증 시도
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userId, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 2. 토큰 발급 및 Refresh Token 저장 (Rotation 또는 최초 저장)
        TokenResponseDTO tokenResponse = jwtProvider.generateToken(authentication);

        refreshTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        // 이미 존재하면 토큰 값만 업데이트 (Rotation)
                        entity -> entity.updateToken(tokenResponse.getRefreshToken()),
                        // 존재하지 않으면 새로 저장
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .userId(userId)
                                .token(tokenResponse.getRefreshToken())
                                .build())
                );

        return tokenResponse;
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {

        // 1. 토큰 유효성 검사 및 사용자 ID 획득
        String userId = validatePasswordResetToken(token);

        // 수정: 리포지토리에 정의된 정확한 메서드 이름 'findByTokenAndUsedFalse'를 사용합니다.
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new IllegalArgumentException(String.format("유효한 토큰을 찾을 수 없습니다: %s", token)));
        resetToken.useToken();
        passwordResetTokenRepository.save(resetToken); // 사용 플래그 업데이트

        // 3. 사용자 엔티티 조회 및 비밀번호 업데이트
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        // 새 비밀번호 암호화 및 업데이트
        String encodedPassword = passwordEncoder.encode(newPassword);

        // User 엔티티의 updatePassword 메서드를 사용하여 업데이트
        user.updatePassword(encodedPassword);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public TokenResponseDTO reissueToken(String refreshToken) {

        // 1. Refresh Token의 유효성 검사 (JwtProvider에서 만료 여부, 형식 등을 검사)
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 Refresh Token입니다. 재로그인이 필요합니다.");
        }

        // 2. DB에서 Refresh Token 정보 조회 및 사용자 ID 획득
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("DB에서 유효한 Refresh Token 정보를 찾을 수 없습니다."));

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
                .orElseThrow(() -> new IllegalArgumentException("사용자 ID를 찾을 수 없습니다."));

        // 2. 초기화 토큰 생성 (UUID 사용)
        String resetToken = generateUniqueResetToken();

        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);

        // 💡 수정: PasswordResetToken.builder()를 사용하여 User 객체를 참조하도록 변경
        PasswordResetToken tokenEntity = PasswordResetToken.builder()
                .user(user) // User 객체 직접 참조
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
                .orElseThrow(() -> new IllegalArgumentException(String.format("유효한 토큰을 찾을 수 없습니다: %s", token)));

        // 2. 토큰 사용 여부 및 만료 시간 확인
        if (resetToken.isUsed() || resetToken.isExpired()) {
            // 이미 사용되었거나 만료된 경우
            throw new IllegalArgumentException("이미 사용되었거나 만료된 토큰입니다.");
        }

        // 3. 검증 성공 시 사용자 ID 반환
        // 💡 수정: User 엔티티에서 ID를 추출하도록 변경
        return resetToken.getUser().getUserId();
    }

    /**
     * 사용자 회원가입을 처리하고, 생성된 사용자의 ID를 반환합니다.
     */
    @Override
    @Transactional
    public String registerNewUser(UserRegisterRequestDTO request) {

        // 1️⃣ 중복 체크
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new DuplicateUserException("이미 존재하는 사용자 ID입니다: " + request.getUserId());
        }

        // 2️⃣ 기본 역할(Role) 부여
        //    → 예: 회원가입 시 ROLE_CLERK 자동 할당
        Role defaultRole = roleRepository.findByRoleId("CLERK")
                .orElseThrow(() -> new IllegalStateException("기본 역할(CLEREK)을 찾을 수 없습니다."));

        // (선택) Store 연계가 필요하다면 추가
        Store store = null;
        if (request.getStoreId() != null) {
            store = storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new IllegalArgumentException("Store not found with ID: " + request.getStoreId()));
        }

        // 3️⃣ 새 사용자 엔티티 생성
        User user = User.builder()
                .userId(request.getUserId())
                .name(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(defaultRole)  // ✅ 기본 ROLE 설정
                .store(store)       // ✅ 선택적 지점 설정
                .enabled(true)
                .build();

        // 4️⃣ 저장 후 반환
        userRepository.save(user);
        return user.getUserId();
    }

}