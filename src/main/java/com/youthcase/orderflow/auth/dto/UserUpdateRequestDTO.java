package com.youthcase.orderflow.auth.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class UserUpdateRequestDTO {

    // ----------------------------------------------------------------------
    // 공통 업데이트 필드 (MyPage, Admin 공통)
    // ----------------------------------------------------------------------

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    // ----------------------------------------------------------------------
    // ⭐️ MyPage 정보 수정을 위한 추가 필드 ⭐️
    // ----------------------------------------------------------------------

    /**
     * MyPage에서 정보를 수정할 때, 사용자 본인의 현재 비밀번호를 검증하기 위한 필드입니다. (필수)
     */
    @NotBlank(message = "현재 비밀번호는 필수 입력 항목입니다.")
    private String currentPassword;

    /**
     * MyPage에서 새로운 비밀번호를 설정할 때 사용하는 필드입니다. (선택적)
     */
    @Size(min = 8, message = "새 비밀번호는 최소 8자 이상이어야 합니다.")
    private String newPassword;

    // ----------------------------------------------------------------------
    // Admin 전용 필드 (MyPage에서 사용 시 무시됨)
    // ----------------------------------------------------------------------

    /**
     * 관리자가 직급(Role ID)을 변경할 때 사용합니다. (Admin 전용)
     */
    @NotBlank(message = "직급 ID는 필수입니다.")
    private String roleId;

    /**
     * 관리자가 담당 Store ID를 변경할 때 사용합니다. (Admin 전용)
     */
    private String storeId; // Nullable
}
