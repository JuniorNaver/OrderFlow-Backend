package com.youthcase.orderflow.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 관리자용 신규 계정 생성 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class AccountCreateRequestDTO {

    @NotBlank(message = "사용자 ID는 필수 입력값입니다.")
    @Size(min = 4, max = 50, message = "ID는 4자 이상 50자 이하로 입력해야 합니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password; // Service에서 반드시 해싱 필요

    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    private String workspace;

    private String storeId;

    // 초기 역할 설정 (필수 또는 기본값 설정 가능)
    @NotBlank(message = "초기 역할 설정은 필수입니다.")
    private String initialRoleId;

    // 관리자가 계정 생성 시 초기 활성화 상태를 지정할 수 있도록 함
    private boolean enabled = true;
    private boolean locked = false;
}
