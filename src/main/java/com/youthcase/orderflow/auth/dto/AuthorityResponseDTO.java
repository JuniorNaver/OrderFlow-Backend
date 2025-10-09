package com.youthcase.orderflow.auth.dto;

import com.youthcase.orderflow.auth.domain.Authority;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder // 빌더 패턴을 사용하여 객체 생성
public class AuthorityResponseDTO {

    private Long authorityId;
    private String authority;
    private String url;

    /**
     * Authority 엔티티를 AuthorityResponseDTO로 변환하는 정적 팩토리 메서드
     * @param authority 변환할 Authority 엔티티
     * @return AuthorityResponseDTO 객체
     */
    public static AuthorityResponseDTO from(Authority authority) {
        return AuthorityResponseDTO.builder()
                // 💡 authority.getId()로 수정 (엔티티 PK 필드명에 맞춤)
                .authorityId(authority.getId())
                .authority(authority.getAuthority())
                // 💡 authority.getUrl() 호출이 가능하도록 Authority 엔티티에 url 필드가 있다고 가정
                .url(authority.getUrl())
                .build();
    }
}