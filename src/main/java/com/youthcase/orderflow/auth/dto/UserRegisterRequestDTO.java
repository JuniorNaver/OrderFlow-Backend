package com.youthcase.orderflow.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 회원가입 요청 DTO
 * - /api/auth/register 엔드포인트에서 사용됩니다.
 * - User 엔티티의 필드와 Role, Store 연계를 고려합니다.
 */
@Getter
@Setter
public class UserRegisterRequestDTO {

    @NotBlank(message = "사용자 ID는 필수입니다.")
    private String userId;

    @NotBlank(message = "사용자 이름은 필수입니다.")
    private String username;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    /**
     * 회원가입 시 선택적으로 소속 지점을 지정합니다.
     */
    private String storeId;  // ✅ 선택 필드

    /**
     * 회원가입 시 역할(Role)을 지정합니다.
     * - 일반 사용자는 null일 경우 시스템에서 기본 ROLE_CLERK이 자동 할당됩니다.
     * - 관리자 회원가입 시 명시적으로 ROLE_ADMIN을 설정할 수 있습니다.
     */
    private String roleId; // ✅ 추가 필드
}
