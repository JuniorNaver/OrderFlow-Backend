package com.youthcase.orderflow.auth.service; // auth 패키지 구조에 맞춤

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

// UserService 인터페이스가 없으므로 여기서는 UserServiceImpl로 직접 구현하며
// 인터페이스의 역할을 수행하는 메서드를 구현합니다. (일반적으로 인터페이스를 분리하는 것이 좋습니다)

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 조회 트랜잭션으로 설정
public class UserServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 새로운 사용자를 시스템에 등록(회원가입)합니다.
     */
    @Transactional // 쓰기 작업이므로 트랜잭션 설정
    public User registerNewUser(String userId, String username, String rawPassword, String workspace, String email, String roleId) {

        // ID 중복 체크 (비즈니스 로직)
        if (userRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 ID입니다: " + userId);
        }

        // 비밀번호 암호화 (핵심 보안 로직)
        String hashedPassword = passwordEncoder.encode(rawPassword);

        // User 엔티티 생성 및 저장
        User newUser = User.builder()
                .userId(userId)
                .username(username)
                .password(hashedPassword)
                .workspace(workspace)
                .email(email)
                .roleId(roleId)
                .build();

        return userRepository.save(newUser);
    }

    /**
     * 사용자 ID로 사용자를 조회합니다.
     */
    public Optional<User> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    /**
     * 사용자의 이름, 근무지, 이메일 정보를 업데이트합니다.
     */
    @Transactional
    public User updateUserDetails(String userId, String username, String workspace, String email) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // User 도메인 객체의 비즈니스 로직(업데이트 메서드) 호출
        user.updateDetails(username, workspace, email);

        // @Transactional로 인해 변경 사항이 DB에 자동 반영됩니다.
        return user;
    }

    /**
     * 사용자의 비밀번호를 변경합니다.
     */
    @Transactional
    public void changePassword(String userId, String newRawPassword) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // 새로운 비밀번호 암호화
        String newHashedPassword = passwordEncoder.encode(newRawPassword);

        // User 도메인 객체의 비즈니스 로직 호출
        user.updatePassword(newHashedPassword);

        // (자동 저장)
    }
}