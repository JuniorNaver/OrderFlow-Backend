package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.domain.UserRole;
import com.youthcase.orderflow.auth.dto.*;
import com.youthcase.orderflow.auth.repository.RoleRepository;
import com.youthcase.orderflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 💡 수정됨: Spring Framework의 Transactional 임포트

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * UserService 인터페이스를 구현하며 사용자 관련 비즈니스 로직을 처리하는 구현체입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 이제 readOnly() 속성을 찾을 수 있습니다.
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository; // 💡 추가: 역할(Role) 조회를 위해 필요
    private final PasswordEncoder passwordEncoder;

    // --- 1. 일반 사용자/인증 관련 메서드 (수정) ---------------------------------

    /**
     * [UserService 구현] 주어진 userId를 사용하여 사용자 상세 정보를 조회하고 UserResponseDTO로 반환합니다.
     * @EntityGraph가 적용된 findByUserId를 사용하여 Role, Authority까지 함께 로드합니다.
     */
    @Override
    public UserResponseDTO getUserDetails(String userId) {
        // findByUserIdWithRoles 대신 findByUserId를 사용하여 EntityGraph의 이점을 얻습니다.
        User user = findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return UserResponseDTO.from(user);
    }

    /**
     * [UserService 구현] Roles 컬렉션까지 함께 Fetch Join으로 로드하는 메서드 (삭제 예정이거나, findByUserId로 대체됨)
     */
    @Override
    public Optional<User> findByUserIdWithRoles(String userId) {
        // [🚨 문제 해결: 이 메서드는 이제 findByUserId(userId)와 동일한 역할을 하므로,
        // UserRepository의 findByUserIdWithRoles 대신 findByUserId를 호출하거나,
        // 이 메서드 자체를 UserService 인터페이스에서 제거하는 것이 좋습니다.]

        // 현재는 컴파일 오류를 해결하기 위해 findByUserId로 대체합니다.
        return userRepository.findByUserId(userId);
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

        user.updateDetails(name, workspace, email);
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

        String newHashedPassword = passwordEncoder.encode(newRawPassword);
        user.updatePassword(newHashedPassword);
        // user.recordPasswordChange(); // User 엔티티의 updatePassword에 이미 포함됨
    }


    // --- 2. 관리자 계정 관리 (Account Admin) 메서드 (추가) ----------------------------

    /**
     * [관리자용] 전체 사용자 목록을 조회하고 검색어에 따라 필터링합니다.
     */
    @Override
    public List<AccountAdminResponseDTO> findAllUsersForAdmin(String search) {
        List<User> users;
        if (search != null && !search.trim().isEmpty()) {
            String searchTerm = "%" + search.trim() + "%";
            // UserRepository에 추가된 통합 검색 메서드 사용
            users = userRepository.findAllByUserIdContainingOrNameContainingOrEmailContaining(
                    searchTerm, searchTerm, searchTerm
            );
        } else {
            users = userRepository.findAll();
        }

        return users.stream()
                .map(AccountAdminResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * [관리자용] 새로운 계정을 생성하고 초기 역할과 상태를 설정합니다.
     */
    @Override
    @Transactional
    public AccountAdminResponseDTO createAccountByAdmin(AccountCreateRequestDTO request) {

        // 1. 사용자 ID 중복 검사 (선택 사항, 필요시 추가)
        if (userRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new IllegalArgumentException("User ID already exists: " + request.getUserId());
        }

        // 2. 초기 역할 조회 및 검증
        Role initialRole = roleRepository.findById(request.getInitialRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Role ID: " + request.getInitialRoleId()));

        // 3. 비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 4. User 엔티티 생성
        User newUser = User.builder()
                .userId(request.getUserId())
                .password(hashedPassword)
                .name(request.getName())
                .email(request.getEmail())
                .workspace(request.getWorkspace())
                .storeId(request.getStoreId())
                .enabled(request.isEnabled())
                .locked(request.isLocked())
                // .passwordChangedAt()는 @Builder.Default로 자동 설정
                .build();

        // 5. UserRole 매핑 생성 및 연결
        UserRole userRole = UserRole.builder().user(newUser).role(initialRole).build();
        newUser.getUserRoles().add(userRole);

        User savedUser = userRepository.save(newUser);
        return new AccountAdminResponseDTO(savedUser);
    }

    /**
     * [관리자용] 기존 계정의 정보, 상태, 역할을 업데이트합니다.
     */
    @Override
    @Transactional
    public AccountAdminResponseDTO updateAccountByAdmin(String userId, AccountUpdateRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // 1. 기본 정보 및 상태 업데이트 (User 엔티티의 비즈니스 메서드 사용)
        user.updateAccountByAdmin(
                request.getName(),
                request.getEmail(),
                request.getWorkspace(),
                request.getStoreId(),
                request.getEnabled(),
                request.getLocked()
        );

        // 2. 역할 매핑 업데이트 (기존 역할 삭제 후 새 역할 부여)
        if (request.getRoleIds() != null) {
            // orphanRemoval = true 설정 덕분에 clear()만으로 기존 UserRole이 삭제됩니다.
            user.getUserRoles().clear();

            Set<UserRole> newUserRoles = request.getRoleIds().stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new IllegalArgumentException("Invalid Role ID: " + roleId)))
                    .map(role -> UserRole.builder().user(user).role(role).build())
                    .collect(Collectors.toSet());

            user.getUserRoles().addAll(newUserRoles);
        }

        // 트랜잭션 커밋 시 자동 반영되지만, 명시적으로 save를 호출할 수도 있습니다.
        return new AccountAdminResponseDTO(user);
    }

    /**
     * [관리자용] 사용자 ID 목록을 받아 일괄적으로 계정을 삭제합니다.
     */
    @Override
    @Transactional
    public void deleteUsersBatch(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) return;

        // JpaRepository의 기본 메서드 활용
        userRepository.deleteAllById(userIds);
    }

    /**
     * [관리자용] 개별 사용자 계정을 삭제합니다.
     */
    @Override
    @Transactional
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    /**
     * [관리자용] 특정 계정의 비밀번호를 강제로 재설정합니다.
     */
    @Override
    @Transactional
    public void resetPasswordByAdmin(String userId, UserPasswordResetByAdminDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // 1. 새 비밀번호 해싱
        String newHashedPassword = passwordEncoder.encode(request.getNewPassword());

        // 2. 비밀번호 업데이트 및 변경 시간 기록
        user.updatePassword(newHashedPassword);

        // (선택 사항) 알림 로직: if (request.isNotifyUser()) { /* 알림 로직 */ }
    }
}
