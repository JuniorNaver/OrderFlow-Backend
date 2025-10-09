package com.youthcase.orderflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthorityRequestDTO {


    @NotBlank(message = "권한명(authority)은 필수입니다.")
    private String authority;


    @NotBlank(message = "URL 패턴은 필수입니다.")
    private String url;

    // 💡 선택적: 특정 HTTP 메서드에만 적용되도록 하려면 추가합니다.
    private String httpMethod;
}