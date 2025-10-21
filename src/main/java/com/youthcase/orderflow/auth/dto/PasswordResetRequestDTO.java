// PasswordResetRequestDTO.java (참고)
package com.youthcase.orderflow.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequestDTO {
    private String userId; // 사용자가 입력한 ID
    private String email;  // 사용자가 입력한 이메일
}