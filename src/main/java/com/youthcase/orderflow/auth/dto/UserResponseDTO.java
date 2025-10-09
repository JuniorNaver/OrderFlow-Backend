package com.youthcase.orderflow.auth.dto;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.domain.Role;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 로그인된 사용자 본인 정보를 클라이언트에 안전하게 반환하기 위한 응답 DTO입니다.
 * 비밀번호 등 민감한 정보는 제외됩니다.
 */
@Getter
@Builder
public class UserResponseDTO {

    private final String userId;
    private final String name;
    private final String email;
    private final Set<String> roles; // 사용자의 역할 목록 (ROLE_ADMIN, ROLE_MANAGER 등)
    private final LocalDateTime createdAt;

    /**
     * User 엔티티를 UserResponseDTO로 변환하는 정적 팩토리 메서드.
     * @param user 변환할 User 엔티티
     * @return UserResponseDTO 객체
     */
    public static UserResponseDTO from(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getRoleId) // Role 엔티티에 getRoleId()가 있다고 가정
                .collect(Collectors.toSet());

        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(roleNames)
                .createdAt(user.getCreatedAt()) // User 엔티티에 createdAt 필드가 있다고 가정
                .build();
    }
}
