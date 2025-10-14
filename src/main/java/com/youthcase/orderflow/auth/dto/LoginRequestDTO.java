package com.youthcase.orderflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor; // 기본 생성자 추가
import lombok.Setter; // Setter 추가 (JSON 바인딩을 위해 필요)

@Getter
@Setter // ⭐️ JSON 바인딩을 위해 Setter 추가
@NoArgsConstructor // ⭐️ 기본 생성자 추가
public class LoginRequestDTO {

    @NotBlank(message = "사용자 ID는 필수 입력값입니다.")
    private String userId; // ⭐️ 프런트엔드에서 'userId'로 전송해야 함

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
}