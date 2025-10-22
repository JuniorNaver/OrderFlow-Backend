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

    // ⭐️ [U] MyPage 정보 수정을 위한 메서드 추가 (일반 사용자용) ⭐️
    /**
     * [U] 현재 인증된 사용자 본인의 정보를 수정합니다. (MyPage 기능)
     * 이 메서드는 반드시 기존 비밀번호 검증 로직을 포함해야 합니다.
     * @param userId 현재 인증된 사용자의 ID
     * @param requestDTO 수정 요청 데이터 (이름, 이메일, 연락처, 현재 비밀번호, 새 비밀번호 등)
     * @return 수정된 UserResponseDTO
     */
    UserResponseDTO updateMyDetails(String userId, UserUpdateRequestDTO requestDTO);

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
     * 주어진 ID의 사용자 정보를 업데이트합니다. (Admin 전용)
     * * @param userId 사용자 PK (String)
     * @param requestDTO 업데이트 요청 데이터
     * @return 업데이트된 계정의 DTO
     */
    UserResponseDTO updateUser(String userId, UserUpdateRequestDTO requestDTO);

    /**
     * 주어진 ID 목록의 사용자 계정을 일괄 삭제합니다.
     * * @param userIds 삭제할 사용자 PK 목록 (String)
     */
    void deleteUsers(List<String> userIds);

    void deleteUser(String userId);
}
