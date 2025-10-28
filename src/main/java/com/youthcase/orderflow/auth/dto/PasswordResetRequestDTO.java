package com.youthcase.orderflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 비밀번호 초기화 이메일 요청 시, 사용자 ID를 받기 위한 DTO
 */
@Getter
public class PasswordResetRequestDTO {
    @NotBlank(message = "사용자 ID는 필수 입력값입니다.")
    private String userId;

    // ⭐️ 오류 해결: email 필드를 추가합니다.
    private String email;
}
