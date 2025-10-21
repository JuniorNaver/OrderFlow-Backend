package com.youthcase.orderflow.auth.dto;

import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 관리자용 계정 목록 조회 및 상세 조회 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class AccountAdminResponseDTO {

    private String userId;
    private String name;
    private String email;
    private String storeId;
    private String workspace;

    // 계정 상태 정보
    private boolean enabled;
    private boolean locked;

    // 역할 정보 (관리자 화면에서 역할명을 표시하기 위해 사용)
    private Set<String> roles;

    // 감사/시간 정보
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private LocalDateTime passwordChangedAt;

    @Builder
    public AccountAdminResponseDTO(User user) {
        this.userId = user.getUserId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.storeId = user.getStoreId();
        this.workspace = user.getWorkspace();
        this.enabled = user.isEnabled();
        this.locked = user.isLocked();
        this.createdAt = user.getCreatedAt();
        this.lastLoginAt = user.getLastLoginAt();
        this.passwordChangedAt = user.getPasswordChangedAt();

        // Role 엔티티 Set을 Role ID (String) Set으로 변환
        this.roles = user.getRoles().stream()
                .map(Role::getRoleId)
                .collect(Collectors.toSet());
    }
}
