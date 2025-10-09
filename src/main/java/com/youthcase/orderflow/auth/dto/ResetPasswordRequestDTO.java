package com.youthcase.orderflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 비밀번호 초기화 토큰과 새 비밀번호를 받아 비밀번호를 업데이트하기 위한 DTO
 */
@Getter
public class ResetPasswordRequestDTO {

    @NotBlank(message = "토큰은 필수 입력값입니다.")
    private String token;

    @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String newPassword;
}
