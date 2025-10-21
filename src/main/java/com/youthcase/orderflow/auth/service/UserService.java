package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.dto.*;

import java.util.List;
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

    // --- 💡 관리자 계정 관리 (Account Admin) 메서드 추가 ---

    /**
     * 관리자용: 전체 사용자 목록을 조회하고 검색어에 따라 필터링합니다.
     */
    List<AccountAdminResponseDTO> findAllUsersForAdmin(String search);

    /**
     * 관리자용: 새로운 계정을 생성하고 초기 역할과 상태를 설정합니다.
     */
    AccountAdminResponseDTO createAccountByAdmin(AccountCreateRequestDTO request);

    /**
     * 관리자용: 기존 계정의 정보, 상태, 역할을 업데이트합니다.
     */
    AccountAdminResponseDTO updateAccountByAdmin(String userId, AccountUpdateRequestDTO request);

    /**
     * 관리자용: 사용자 ID 목록을 받아 일괄적으로 계정을 삭제합니다.
     */
    void deleteUsersBatch(List<String> userIds);

    /**
     * 관리자용: 개별 사용자 계정을 삭제합니다.
     */
    void deleteUser(String userId);

    /**
     * 관리자용: 특정 계정의 비밀번호를 강제로 재설정합니다.
     */
    void resetPasswordByAdmin(String userId, UserPasswordResetByAdminDTO request);
}