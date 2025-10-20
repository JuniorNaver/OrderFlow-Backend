package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.dto.UserResponseDTO;

import java.util.Optional;

public interface UserService {

    /**
     * 주어진 userId를 사용하여 사용자 상세 정보를 조회하고 UserResponseDTO로 반환합니다.
     *
     * @param userId 현재 인증된 사용자의 ID
     * @return 사용자 상세 정보가 담긴 UserResponseDTO
     */
    UserResponseDTO getUserDetails(String userId);

    // 💡 추가: Roles 컬렉션까지 함께 Fetch Join으로 로드하는 메서드
    /**
     * 사용자 ID로 User 엔티티를 조회합니다. (Roles 컬렉션을 EAGER 로딩)
     * @param userId 조회할 사용자 ID
     * @return User 엔티티 (Optional)
     */
    Optional<User> findByUserIdWithRoles(String userId);

    /**
     * 사용자 ID로 User 엔티티를 조회합니다. (일반 LAZY 로딩)
     * @param userId 조회할 사용자 ID
     * @return User 엔티티 (Optional)
     */
    Optional<User> findByUserId(String userId);

    // ... (나머지 메서드 유지) ...
    User updateUserDetails(String userId, String username, String workspace, String email);
    void changePassword(String userId, String newRawPassword);
}