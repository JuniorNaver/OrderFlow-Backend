package com.youthcase.orderflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor; // 💡 @Setter 대신 추가

/**
 * 권한 생성 및 수정을 위한 요청 DTO
 */
@Getter // Getter 유지 (읽기 전용 목적)
@NoArgsConstructor
@AllArgsConstructor // 모든 필드를 받는 생성자 추가
public class AuthorityRequestDTO {


    @NotBlank(message = "권한명(authority)은 필수입니다.")
    private String authority;

    // 💡 필드명을 urlPattern으로 수정하여 일관성 확보
    @NotBlank(message = "URL 패턴은 필수입니다.")
    private String urlPattern;

    // 💡 Authority 엔티티를 고려하여 description 필드 추가
    private String description;

    // 선택적: 특정 HTTP 메서드에만 적용되도록 하려면 추가합니다.
    private String httpMethod;
}