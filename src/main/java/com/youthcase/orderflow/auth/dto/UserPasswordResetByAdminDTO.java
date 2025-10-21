package com.youthcase.orderflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 관리자가 특정 계정의 비밀번호를 강제로 초기화하거나 변경 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class UserPasswordResetByAdminDTO {

    // userId는 PathVariable로 받거나, 이 DTO에 포함시킬 수 있습니다.
    // 여기서는 PathVariable로 받는 것을 가정하고 새로운 비밀번호만 정의합니다.

    @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
    @Size(min = 8, message = "새 비밀번호는 최소 8자 이상이어야 합니다.")
    private String newPassword; // Service에서 반드시 해싱 필요

    // 선택 사항: 비밀번호 변경 후 사용자에게 이메일 알림을 보낼지 여부
    private boolean notifyUser = false;
}
