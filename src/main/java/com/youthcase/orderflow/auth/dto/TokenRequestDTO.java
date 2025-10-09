package com.youthcase.orderflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TokenRequestDTO {

    @NotBlank(message = "Refresh Token은 필수입니다.")
    private String refreshToken;
}
