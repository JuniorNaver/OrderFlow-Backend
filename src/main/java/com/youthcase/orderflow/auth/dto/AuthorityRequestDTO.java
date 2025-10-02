package com.youthcase.orderflow.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 참고: 실제 사용 시 @NotBlank, @Size 등 유효성 검사(@Valid) 어노테이션을 추가해야 합니다.
@Getter
@Setter
@NoArgsConstructor // 기본 생성자
public class AuthorityRequestDTO {

    // 권한명 (예: STK_WRITE, ORDER_READ)
    private String authority;

    // 권한이 적용되는 URL 패턴 (예: /api/stock/**)
    private String url;

    // 이 DTO는 생성 및 수정 요청의 Body에 사용됩니다.
}