package com.youthcase.orderflow.auth.service; // 🚨 구현체는 impl 패키지에 위치하는 것이 일반적입니다.

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.repository.UserRepository;
import com.youthcase.orderflow.auth.service.UserService; // 🚨 인터페이스 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 조회 트랜잭션으로 설정
public class UserServiceImpl implements UserService { // 🚨 UserService 인터페이스 구현 명시

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 🚨 registerNewUser 메서드는 AuthService로 이동되었으므로 제거합니다.

    /**
     * 사용자 ID로 사용자를 조회합니다.
     */
    @Override
    public Optional<User> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    /**
     * 사용자의 이름, 근무지, 이메일 정보를 업데이트합니다.
     * (로그인된 사용자 본인이 자신의 정보를 수정할 때 사용)
     */
    @Override
    @Transactional
    public User updateUserDetails(String userId, String username, String workspace, String email) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // User 도메인 객체의 비즈니스 로직(업데이트 메서드) 호출
        // 💡 User 엔티티에 updateDetails(username, workspace, email) 메서드가 구현되어 있어야 합니다.
        user.updateDetails(username, workspace, email);

        // @Transactional로 인해 변경 사항이 DB에 자동 반영됩니다.
        return user;
    }

    /**
     * 사용자의 비밀번호를 변경합니다.
     * (로그인된 사용자 본인이 자신의 비밀번호를 수정할 때 사용)
     */
    @Override
    @Transactional
    public void changePassword(String userId, String newRawPassword) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // 새로운 비밀번호 암호화
        String newHashedPassword = passwordEncoder.encode(newRawPassword);

        // User 도메인 객체의 비즈니스 로직 호출
        // 💡 User 엔티티에 updatePassword(newHashedPassword) 메서드가 구현되어 있어야 합니다.
        user.updatePassword(newHashedPassword);

        // (자동 저장)
    }
}
