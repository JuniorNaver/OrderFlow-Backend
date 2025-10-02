package com.youthcase.orderflow.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResetPasswordRequestDTO {

    // 이메일을 통해 전달받은 초기화 토큰
    private String token;

    // 사용자가 새로 설정할 비밀번호
    private String newPassword;

    // (선택적) 새 비밀번호 확인 (프론트엔드에서 일치 확인 권장)
    // private String confirmPassword;
}