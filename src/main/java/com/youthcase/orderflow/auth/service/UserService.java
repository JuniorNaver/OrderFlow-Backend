package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.domain.User;
// import com.youthcase.orderflow.auth.dto.UserRegisterRequestDTO; // 제거: registerUser 메서드가 제거됨
import java.util.Optional;

public interface UserService {

    //  제거: registerUser(UserRegisterRequestDTO) 메서드를 제거하여 UerServiceImpl의 구현 누락 에러 해결

    /**
     * 사용자 ID로 User 엔티티를 조회합니다.
     * @param userId 조회할 사용자 ID
     * @return User 엔티티 (Optional)
     */
    Optional<User> findByUserId(String userId);

    /**
     * 사용자의 이름, 근무지, 이메일 정보를 업데이트합니다. ( 신규 추가)
     */
    User updateUserDetails(String userId, String username, String workspace, String email);

    /**
     * 로그인된 사용자 본인의 비밀번호를 변경합니다.
     * @param userId 비밀번호를 변경할 사용자 ID
     * @param newRawPassword 새로운 비밀번호 (인코딩 전)
     */
    void changePassword(String userId, String newRawPassword);
}
// -- END --