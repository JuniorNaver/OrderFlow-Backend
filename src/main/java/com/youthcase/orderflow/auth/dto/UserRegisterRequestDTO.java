package com.youthcase.orderflow.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

// @Setter는 보안상 사용하지 않고, @Getter와 @NoArgsConstructor만 사용하는 것을 권장합니다.
// Spring은 @RequestBody를 통해 JSON을 이 DTO 객체로 변환할 때 Setter나 NoArgsConstructor를 사용합니다.
@Getter
@NoArgsConstructor // JSON 역직렬화를 위해 필요
public class UserRegisterRequestDTO {

    private String userId;      // 계정ID (USER_ID)
    private String username;    // 이름 (USERNAME)
    private String password;    // 비밀번호 (Raw Password)
    private String workspace;   // 근무지 (WORKSPACE)
    private String email;       // 이메일 (EMAIL)
    private String roleId;      // 역할ID (ROLE_ID) - 기본 역할 설정을 위해 필요
}