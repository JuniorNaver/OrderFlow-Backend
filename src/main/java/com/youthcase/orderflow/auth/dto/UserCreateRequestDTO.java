package com.youthcase.orderflow.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 관리자용 사용자 계정 생성 요청 DTO.
 * UserServiceImpl의 createUser() 메서드에서 사용됩니다.
 */
@Getter
public class UserCreateRequestDTO {

    @NotBlank(message = "사용자 ID는 필수입니다.")
    private String userId; // 👈 getUserId() 오류 해결

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password; // 👈 getPassword() 오류 해결

    @NotBlank(message = "사용자 이름은 필수입니다.")
    private String name; // 👈 getName() 오류 해결

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email; // 👈 getEmail() 오류 해결

    private String position;

    private String workspace; // 👈 getWorkspace() 오류 해결

    @NotBlank(message = "직급(Role ID)은 필수입니다.")
    private String roleId;

    private Long storeId;
}
