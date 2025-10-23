package com.youthcase.orderflow.auth.dto;

import com.youthcase.orderflow.auth.domain.Authority;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthorityResponseDTO {

    private Long authorityId;
    private String authority;
    // 💡 필드명을 Request DTO, Entity와 동일하게 urlPattern으로 변경
    private String urlPattern;

    public static AuthorityResponseDTO from(Authority authority) {
        return AuthorityResponseDTO.builder()
                .authorityId(authority.getAuthorityId())
                .authority(authority.getAuthority())
                // 💡 Getter도 urlPattern에 맞춥니다.
                .urlPattern(authority.getUrlPattern())
                .build();
    }
}