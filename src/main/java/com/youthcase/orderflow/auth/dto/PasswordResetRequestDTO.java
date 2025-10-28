// 📁 com.youthcase.orderflow.auth.dto.PasswordResetRequestDTO

package com.youthcase.orderflow.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 비밀번호 초기화 이메일 요청 시, 사용자 ID와 Email을 받기 위한 DTO
 */
@Getter
public class PasswordResetRequestDTO {
    @NotBlank(message = "사용자 ID는 필수 입력값입니다.")
    private String userId;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;
}
