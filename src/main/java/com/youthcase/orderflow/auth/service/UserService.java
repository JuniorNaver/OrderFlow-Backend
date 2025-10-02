package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.user.domain.User;
import java.util.Optional;

public interface UserService {

    /**
     * 새로운 사용자를 시스템에 등록(회원가입)합니다.
     * @param userId 계정 ID
     * @param username 사용자 이름
     * @param rawPassword 암호화되지 않은 비밀번호 (서비스 계층에서 암호화 처리)
     * @param workspace 근무지
     * @param email 이메일
     * @param roleId 기본 역할 ID
     * @return 등록된 User 엔티티
     */
    User registerNewUser(String userId, String username, String rawPassword, String workspace, String email, String roleId);

    /**
     * 사용자 ID로 사용자를 조회합니다.
     * @param userId 계정 ID
     * @return User 엔티티 (Optional)
     */
    Optional<User> findByUserId(String userId);

    /**
     * 사용자의 이름, 근무지, 이메일 정보를 업데이트합니다.
     * @param userId 업데이트할 사용자 ID
     * @param username 새로운 사용자 이름
     * @param workspace 새로운 근무지
     * @param email 새로운 이메일
     * @return 업데이트된 User 엔티티
     */
    User updateUserDetails(String userId, String username, String workspace, String email);

    /**
     * 사용자의 비밀번호를 변경합니다.
     * @param userId 비밀번호를 변경할 사용자 ID
     * @param newRawPassword 새로 설정할 암호화되지 않은 비밀번호
     */
    void changePassword(String userId, String newRawPassword);

    // 이메일 중복 체크, 사용자 목록 조회 등 필요한 기능 추가 가능
}