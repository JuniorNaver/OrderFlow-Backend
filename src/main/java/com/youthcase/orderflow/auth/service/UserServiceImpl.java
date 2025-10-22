package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.domain.UserRole;
import com.youthcase.orderflow.auth.dto.UserCreateRequestDTO;
import com.youthcase.orderflow.auth.dto.UserResponseDTO;
import com.youthcase.orderflow.auth.dto.UserUpdateRequestDTO;
import com.youthcase.orderflow.auth.repository.RoleRepository;
import com.youthcase.orderflow.auth.repository.UserRepository;
import com.youthcase.orderflow.auth.repository.UserRoleRepository;
import com.youthcase.orderflow.global.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserService 인터페이스를 구현하며 사용자 관련 비즈니스 로직을 처리하는 구현체입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 변경을 위해 필요하다고 가정

    // -------------------------------------------------------------------------
    // 일반 사용자 (GET, PWD, Find)
    // -------------------------------------------------------------------------

    /**
     * [UserService 구현] 주어진 userId를 사용하여 사용자 상세 정보를 조회하고 UserResponseDTO로 반환합니다.
     */
    @Override
    public UserResponseDTO getUserDetails(String userId) {

        // UserResponseDTO 생성을 위해 WithRoles 메서드를 사용하여 Fetch Join된 User를 가져옵니다.
        User user = findByUserIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        return UserResponseDTO.from(user);
    }

    /**
     * [UserService 구현] Roles 컬렉션까지 함께 Fetch Join으로 로드하는 메서드 (Repository에 위임)
     */
    @Override
    public Optional<User> findByUserIdWithRoles(String userId) {
        return userRepository.findByUserIdWithRoles(userId);
    }

    /**
     * [UserService 구현] 사용자 ID로 User 엔티티를 조회합니다. (Repository에 위임)
     */
    @Override
    public Optional<User> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    // ⭐️ 오류가 발생했던 updateUserDetails 메서드는 Admin 업데이트 로직과 충돌/혼란을 막기 위해 제거했습니다.
    // 71행의 오류가 발생했던 코드가 포함된 메서드입니다.
    /*
    @Override
    @Transactional
    public User updateUserDetails(String userId, String name, String workspace, String email) {
        User user = findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for update with ID: " + userId));

        // user.updateDetails(name, workspace, email, storeId); // 71행 오류의 근원지
        user.updateDetails(name, workspace, email, user.getStoreId()); // 임시 수정

        return user;
    }
    */

    // ⭐️ 기존 인터페이스에 남아있던 updateUserDetails 구현체는 충돌 방지를 위해 제거되었습니다.
    // 대신 Admin 기능을 위한 updateUser를 사용합니다.
    @Transactional
    public UserResponseDTO updateUserDetails(String userId, UserUpdateRequestDTO requestDTO, Long storeId) {
        throw new UnsupportedOperationException("이 메서드는 사용되지 않습니다. updateUser를 사용하세요.");
    }

    /**
     * [UserService 구현] 사용자 비밀번호를 변경합니다.
     */
    @Override
    @Transactional
    public void changePassword(String userId, String newRawPassword) {
        User user = findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for password change with ID: " + userId));

        String newHashedPassword = passwordEncoder.encode(newRawPassword);

        user.updatePassword(newHashedPassword);
    }

    // ----------------------------------------------------------------------
    // [AdminUserController 지원을 위한 CRUD 메서드 구현]
    // ----------------------------------------------------------------------

    /**
     * 모든 계정 또는 검색 조건에 맞는 계정 목록을 조회합니다.
     */
    @Override
    public List<UserResponseDTO> getAllUsers(String search) {
        List<User> users;

        if (search != null && !search.trim().isEmpty()) {
            // UserRepository의 findByUserIdContainingOrNameContaining 쿼리 메서드 사용
            users = userRepository.findByUserIdContainingOrNameContaining(search, search);
        } else {
            users = userRepository.findAll();
        }

        return users.stream()
                .map(UserResponseDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 사용자 계정을 생성합니다.
     */
    @Override
    @Transactional
    public UserResponseDTO createUser(UserCreateRequestDTO requestDTO) {
        // DTO 필드 오류 해결 완료
        if (userRepository.existsByUserId(requestDTO.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 사용자 ID입니다: " + requestDTO.getUserId());
        }

        User newUser = User.builder()
                .userId(requestDTO.getUserId())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .name(requestDTO.getName())
                .email(requestDTO.getEmail())
                .workspace(requestDTO.getWorkspace())
                .storeId(requestDTO.getStoreId())
                .enabled(true)
                .build();

        User savedUser = userRepository.save(newUser);



        // 1. 할당할 Role 엔티티 조회
        Role assignedRole = roleRepository.findByRoleId(requestDTO.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + requestDTO.getRoleId()));

        // 2. UserRole 매핑 엔티티 생성
        UserRole userRole = UserRole.builder()
                .user(savedUser) // 새로 저장된 User 엔티티
                .role(assignedRole) // 조회된 Role 엔티티
                .build(); // assignedAt은 UserRole 엔티티의 @Builder에서 기본값으로 처리됨

        // 3. UserRole 저장 (USER_ROLE 테이블에 레코드 삽입)
        userRoleRepository.save(userRole);



        // 4. 응답 반환 시, UserResponseDTO.from()은 이제 UserRole 컬렉션에서 Role을 찾을 수 있습니다.
        return UserResponseDTO.from(savedUser);
    }

    /**
     * 주어진 ID의 사용자 정보를 업데이트합니다. (Admin 기능)
     * 💡 Admin 기능이므로 roleId 및 storeId 업데이트 로직을 포함합니다.
     */
    @Override
    @Transactional
    public UserResponseDTO updateUser(String userId, UserUpdateRequestDTO requestDTO) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // 1. 사용자 기본 정보 업데이트 (name, workspace, email, storeId)
        user.updateDetails(
                requestDTO.getName(),
                requestDTO.getWorkspace(),
                requestDTO.getEmail(),
                requestDTO.getStoreId()
        );

        // 2. ⭐️ 역할(Role) 업데이트 로직 시작 ⭐️

        // 2-1. 기존 역할(UserRole) 매핑을 모두 삭제합니다.
        userRoleRepository.deleteByUserId(userId);

        // 2-2. 새로운 Role 엔티티 조회
        Role newRole = roleRepository.findByRoleId(requestDTO.getRoleId()) // DTO에서 roleId를 가져옴
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + requestDTO.getRoleId()));

        // 2-3. 새로운 UserRole 매핑 생성 및 저장 (새 역할 할당)
        UserRole newUserRole = UserRole.builder()
                .user(user)
                .role(newRole)
                .build();

        userRoleRepository.save(newUserRole);

        // 2. ⭐️ 역할(Role) 업데이트 로직 끝 ⭐️

        // 변경된 사용자 정보를 응답합니다.
        return UserResponseDTO.from(user);
    }

    /**
     * [D] 단일 계정 삭제 (AdminUserController의 /users/{userId} 엔드포인트에 대응)
     */
    @Override
    @Transactional
    public void deleteUser(String userId) {
        if (!userRepository.existsByUserId(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        userRepository.deleteById(userId);
    }

    /**
     * 주어진 ID 목록의 사용자 계정을 일괄 삭제합니다.
     */
    @Override
    @Transactional
    public void deleteUsers(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        userRepository.deleteAllById(userIds);
    }


}