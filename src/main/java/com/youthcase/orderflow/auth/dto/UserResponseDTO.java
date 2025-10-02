package com.youthcase.orderflow.auth.dto;

import com.youthcase.orderflow.auth.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDTO {

    private final String userId;
    private final String username;
    private final String workspace;
    private final String email;
    private final String roleId;    // 역할 ID (필요시 역할 이름 등으로 변경)

    // 엔티티를 DTO로 변환하는 팩토리 메서드
    public static UserResponseDTO from(User user) {
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .workspace(user.getWorkspace())
                .email(user.getEmail())
                .roleId(user.getRoleId())
                // .password(user.getPassword()) // ⭐ 보안을 위해 비밀번호는 절.대. 포함하지 않습니다.
                .build();
    }
}