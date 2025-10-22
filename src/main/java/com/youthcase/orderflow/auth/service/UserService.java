package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.dto.UserCreateRequestDTO;
import com.youthcase.orderflow.auth.dto.UserResponseDTO;
import com.youthcase.orderflow.auth.dto.UserUpdateRequestDTO;

import java.util.Optional;
import java.util.List;

public interface UserService {

    /**
     * 주어진 userId를 사용하여 사용자 상세 정보를 조회하고 UserResponseDTO로 반환합니다.
     *
     * @param userId 현재 인증된 사용자의 ID
     * @return 사용자 상세 정보가 담긴 UserResponseDTO
     */
    UserResponseDTO getUserDetails(String userId);

    // 💡 Roles 컬렉션까지 함께 Fetch Join으로 로드하는 메서드
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

    // ⭐️ 기존 updateUserDetails(String, String, String, String) 메서드는 제거되었으므로,
    // 현재 구현체가 사용하는 시그니처를 유지합니다.
    // 기존에 인자 수가 4개였던 updateUserDetails 구현체가 없으므로, @Override 오류를 방지하기 위해
    // Admin 업데이트 메서드와 관련된 메서드만 남깁니다. (주석 처리된 메서드를 삭제했음을 가정)

    // ⭐️ Admin 업데이트 메서드에 StoreId를 DTO가 아닌 별도의 인자로 받는 로직을 제거하고,
    // DTO 내부의 StoreId를 사용하도록 updateUser 메서드를 사용합니다.

    void changePassword(String userId, String newRawPassword);

    // ----------------------------------------------------------------------
    // [AdminUserController 지원을 위한 신규 CRUD 메서드]
    // ----------------------------------------------------------------------

    /**
     * 모든 계정 또는 검색 조건에 맞는 계정 목록을 조회합니다.
     * @param search 검색어 (userId, name 등)
     * @return UserResponseDTO 목록
     */
    List<UserResponseDTO> getAllUsers(String search);

    /**
     * 새로운 사용자 계정을 생성합니다.
     * @param requestDTO 사용자 생성 요청 데이터
     * @return 생성된 계정의 DTO
     */
    UserResponseDTO createUser(UserCreateRequestDTO requestDTO);

    /**
     * 주어진 ID의 사용자 정보를 업데이트합니다.
     * * @param userId 사용자 PK (String)
     * @param requestDTO 업데이트 요청 데이터
     * @return 업데이트된 계정의 DTO
     */
    // ⭐️ 기존 updateUserDetails와 겹치거나 혼란을 줄 수 있는 메서드는 updateUser로 통일하고 DTO만 받도록 합니다.
    // DTO 내부에 storeId가 있으므로 별도 인자 불필요.
    UserResponseDTO updateUser(String userId, UserUpdateRequestDTO requestDTO);

    /**
     * 주어진 ID 목록의 사용자 계정을 일괄 삭제합니다.
     * * @param userIds 삭제할 사용자 PK 목록 (String)
     */
    void deleteUsers(List<String> userIds);

    void deleteUser(String userId);
}