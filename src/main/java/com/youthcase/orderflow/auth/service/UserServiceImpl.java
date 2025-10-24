package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.dto.UserCreateRequestDTO;
import com.youthcase.orderflow.auth.dto.UserResponseDTO;
import com.youthcase.orderflow.auth.dto.UserUpdateRequestDTO;
import com.youthcase.orderflow.auth.repository.RoleRepository;
import com.youthcase.orderflow.auth.repository.UserRepository;
import com.youthcase.orderflow.global.error.ResourceNotFoundException;
import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.store.repository.StoreRepository;
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
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;

    // -------------------------------------------------------------------------
    // 일반 사용자 (GET, PWD, Find, MyPage Update)
    // -------------------------------------------------------------------------

    /**
     * 주어진 userId를 사용하여 사용자 상세 정보를 조회하고 UserResponseDTO로 반환합니다.
     */
    @Override
    public UserResponseDTO getUserDetails(String userId) {
        User user = findByUserIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return UserResponseDTO.from(user);
    }

    /**
     * 현재 인증된 사용자 본인의 정보를 수정합니다. (MyPage 기능)
     */
    @Override
    @Transactional
    public UserResponseDTO updateMyDetails(String userId, UserUpdateRequestDTO requestDTO) {
        User user = findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // 현재 비밀번호 검증
        if (requestDTO.getCurrentPassword() == null ||
                !passwordEncoder.matches(requestDTO.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않아 정보 수정에 실패했습니다.");
        }

        // 일반 정보 업데이트 (store, role은 유지)
        user.updateDetails(
                requestDTO.getName(),
                requestDTO.getEmail(),
                user.getStore(),
                user.getRole()
        );

        // 새 비밀번호가 있으면 변경
        if (requestDTO.getNewPassword() != null && !requestDTO.getNewPassword().isEmpty()) {
            user.updatePassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        }

        return UserResponseDTO.from(userRepository.save(user));
    }

    /**
     * UserRepository에서 Fetch Join으로 Role까지 함께 로드.
     */
    @Override
    public Optional<User> findByUserIdWithRoles(String userId) {
        return userRepository.findByUserIdWithRoles(userId);
    }

    /**
     * userId로 User 조회
     */
    @Override
    public Optional<User> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    /**
     * 사용자 비밀번호 변경
     */
    @Override
    @Transactional
    public void changePassword(String userId, String newRawPassword) {
        User user = findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for password change with ID: " + userId));

        user.updatePassword(passwordEncoder.encode(newRawPassword));
    }

    // ----------------------------------------------------------------------
    // [AdminUserController 지원을 위한 CRUD 메서드]
    // ----------------------------------------------------------------------

    /**
     * 모든 사용자 조회 (검색어 옵션 포함)
     */
    @Override
    public List<UserResponseDTO> getAllUsers(String search) {
        List<User> users;
        if (search != null && !search.trim().isEmpty()) {
            users = userRepository.findByUserIdContainingOrNameContaining(search, search);
        } else {
            users = userRepository.findAll();
        }

        return users.stream()
                .map(UserResponseDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 사용자 생성 (Admin 기능)
     */
    @Override
    @Transactional
    public UserResponseDTO createUser(UserCreateRequestDTO requestDTO) {
        if (userRepository.existsByUserId(requestDTO.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 사용자 ID입니다: " + requestDTO.getUserId());
        }

        // ✅ Store 설정
        Store store = null;
        if (requestDTO.getStoreId() != null) {
            store = storeRepository.findById(requestDTO.getStoreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Store not found with ID: " + requestDTO.getStoreId()));
        }

        // ✅ Role 설정
        Role role = roleRepository.findByRoleId(requestDTO.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + requestDTO.getRoleId()));

        // ✅ User 생성
        User newUser = User.builder()
                .userId(requestDTO.getUserId())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .name(requestDTO.getName())
                .email(requestDTO.getEmail())
                .store(store)
                .role(role) // ✅ 역할 직접 주입
                .enabled(true)
                .build();

        return UserResponseDTO.from(userRepository.save(newUser));
    }

    /**
     * 사용자 정보 수정 (Admin 기능)
     */
    @Override
    @Transactional
    public UserResponseDTO updateUser(String userId, UserUpdateRequestDTO requestDTO) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // ✅ Store 업데이트
        Store newStore = null;
        if (requestDTO.getStoreId() != null) {
            newStore = storeRepository.findById(requestDTO.getStoreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Store not found with ID: " + requestDTO.getStoreId()));
        }

        // ✅ Role 업데이트 (관리자만 변경 가능)
        Role newRole = null;
        if (requestDTO.getRoleId() != null) {
            newRole = roleRepository.findByRoleId(requestDTO.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + requestDTO.getRoleId()));
        }

        user.updateDetails(
                requestDTO.getName(),
                requestDTO.getEmail(),
                newStore,
                newRole
        );

        return UserResponseDTO.from(userRepository.save(user));
    }

    /**
     * 단일 사용자 삭제
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
     * 사용자 일괄 삭제
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
