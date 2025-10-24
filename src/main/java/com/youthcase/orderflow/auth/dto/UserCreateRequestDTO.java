package com.youthcase.orderflow.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 관리자용 사용자 계정 생성 요청 DTO.
 * UserServiceImpl의 createUser() 메서드에서 사용됩니다.
 */
@Getter
@Setter
@ToString
public class UserCreateRequestDTO {

    @NotBlank(message = "사용자 ID는 필수입니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "사용자 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    private String position;

    @NotBlank(message = "직급(Role ID)은 필수입니다.")
    private String roleId;

    /**
     * 프론트엔드에서 점포 ID를 문자열로 전달합니다.
     * 예: "ST001"
     * UserServiceImpl에서 StoreRepository를 통해 실제 Store 엔티티로 변환됩니다.
     */
    private String storeId;
}
