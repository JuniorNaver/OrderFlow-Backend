package com.youthcase.orderflow.auth.dto;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.domain.Role; // Role 엔티티 import 가정
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 사용자 상세 정보를 클라이언트에게 전달하기 위한 Data Transfer Object (DTO).
 */
@Getter
@Builder
public class UserResponseDTO {
    private String userId;
    private String email;
    private String name;
    private String workspace;
    private LocalDateTime createdAt;
    private Set<String> roles; // 사용자 역할을 담을 필드

    /**
     * User 엔티티를 UserResponseDTO로 변환하는 정적 팩토리 메서드.
     * @param user 조회된 User 엔티티 (Fetch Join되어 Role 컬렉션이 로딩되어 있어야 함)
     * @return UserResponseDTO 객체
     */
    public static UserResponseDTO from(User user) {
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .workspace(user.getWorkspace())
                .createdAt(user.getCreatedAt())
                // 💡 해결: Role::getName 대신 Role 엔티티의 실제 Getter인 Role::getRoleId 사용
                .roles(user.getRoles().stream()
                        .map(Role::getRoleId) // 👈 컴파일 오류 해결 지점
                        .collect(Collectors.toSet()))
                .build();
    }
}