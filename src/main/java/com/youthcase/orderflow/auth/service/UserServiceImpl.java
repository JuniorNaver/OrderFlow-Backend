package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.dto.UserResponseDTO;
import com.youthcase.orderflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * UserService 인터페이스를 구현하며 사용자 관련 비즈니스 로직을 처리하는 구현체입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 변경을 위해 필요하다고 가정

    /**
     * [UserService 구현] 주어진 userId를 사용하여 사용자 상세 정보를 조회하고 UserResponseDTO로 반환합니다.
     */
    @Override
    public UserResponseDTO getUserDetails(String userId) {

        // UserResponseDTO 생성을 위해 WithRoles 메서드를 사용하여 Fetch Join된 User를 가져옵니다.
        // Role 정보가 필요한 경우 No Session 오류를 방지하기 위해 Fetch Join이 필수적입니다.
        User user = findByUserIdWithRoles(userId)
                // 사용자를 찾지 못하면 RuntimeException을 발생시키고, 이는 HTTP 500으로 변환될 수 있습니다.
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // 조회된 엔티티를 응답 DTO로 변환하여 반환합니다.
        return UserResponseDTO.from(user);
    }

    /**
     * [UserService 구현] Roles 컬렉션까지 함께 Fetch Join으로 로드하는 메서드 (Repository에 위임)
     */
    @Override
    public Optional<User> findByUserIdWithRoles(String userId) {
        // 이 메서드는 UserRepository에 구현된 fetch join 쿼리를 호출합니다.
        return userRepository.findByUserIdWithRoles(userId);
    }

    /**
     * [UserService 구현] 사용자 ID로 User 엔티티를 조회합니다. (Repository에 위임)
     */
    @Override
    public Optional<User> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    /**
     * [UserService 구현] 사용자 정보를 업데이트합니다.
     */
    @Override
    @Transactional // 수정 작업이므로 트랜잭션 필요
    public User updateUserDetails(String userId, String name, String workspace, String email) {
        User user = findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found for update with ID: " + userId));

        // User 엔티티의 비즈니스 로직 메서드 호출
        user.updateDetails(name, workspace, email);
        // JPA의 영속성 컨텍스트 덕분에 userRepository.save() 호출 없이도 변경이 반영됩니다.

        return user;
    }

    /**
     * [UserService 구현] 사용자 비밀번호를 변경합니다.
     */
    @Override
    @Transactional // 수정 작업이므로 트랜잭션 필요
    public void changePassword(String userId, String newRawPassword) {
        User user = findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found for password change with ID: " + userId));

        // 1. 새 비밀번호를 해시합니다.
        String newHashedPassword = passwordEncoder.encode(newRawPassword);

        // 2. User 엔티티의 비즈니스 로직 메서드 호출
        user.updatePassword(newHashedPassword);

        // (주의: UserRepository.save(user)는 명시적으로 호출하지 않아도 트랜잭션 커밋 시 반영됨)
    }
}
