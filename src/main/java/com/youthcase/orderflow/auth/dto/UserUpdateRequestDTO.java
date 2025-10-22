package com.youthcase.orderflow.auth.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // Long 타입 검증용

@Getter
@Setter // Lombok Setter 추가 (필요하다면)
public class UserUpdateRequestDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String workspace;

    @NotBlank
    @Email
    private String email;

    // ⭐️ 직급(Role ID) 업데이트를 위해 추가
    @NotBlank(message = "직급 ID는 필수입니다.")
    private String roleId;

    // ⭐️ storeId 업데이트를 위해 추가
    private Long storeId; // Nullable일 수 있으므로 @NotNull은 제외
}
