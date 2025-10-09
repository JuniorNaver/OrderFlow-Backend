package com.youthcase.orderflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 로그인된 사용자 본인의 비밀번호 변경 요청을 처리하는 DTO입니다.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserPasswordChangeRequestDTO {

    // 💡 참고: 기존 비밀번호 검증 필드(oldPassword)는 보안상 추가가 권장되나,
    // 현재 컨트롤러와 UserService에는 없으므로, 새 비밀번호만 포함합니다.

    @NotBlank(message = "새 비밀번호는 필수입니다.")
    private String newPassword;

    // 💡 선택적: 비밀번호 확인을 위한 필드를 추가할 수 있습니다.
    // private String confirmPassword;
}
