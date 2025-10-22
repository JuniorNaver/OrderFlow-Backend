package com.youthcase.orderflow.auth.dto;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.domain.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

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
    private boolean enabled; // 💡 활성화 상태 필드 추가 (테이블 표시용)

    // ⭐️ 프론트엔드 AccountManage.jsx 테이블의 '직책' 열에 매핑됩니다.
    private String position;

    // ⭐️ 프론트엔드 AccountManage.jsx 테이블의 '점포 ID' 열에 매핑됩니다.
    private Long storeId;

    /**
     * User 엔티티를 UserResponseDTO로 변환하는 정적 팩토리 메서드.
     * @param user 조회된 User 엔티티
     * @return UserResponseDTO 객체
     */
    public static UserResponseDTO from(User user) {

        // ⭐️ 사용자에게 여러 역할이 있을 경우, 권한이 가장 높은 단일 역할을 추출합니다.
        // 여기서는 ROLE_ADMIN > ROLE_MANAGER > ROLE_CLERK 순으로 가정하고 비교기를 만듭니다.
        String primaryRoleId = user.getRoles().stream()
                .map(Role::getRoleId) // "ROLE_ADMIN", "ROLE_MANAGER" 등을 추출
                .max(Comparator.comparingInt(UserResponseDTO::getRoleOrder)) // 가장 높은 권한을 가진 Role ID를 선택
                .orElse(""); // 역할이 없으면 빈 문자열

        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .workspace(user.getWorkspace())
                .createdAt(user.getCreatedAt())
                .enabled(user.isEnabled())// User 엔티티에 isEnabled()가 있다고 가정
                .position(getRoleNameForUI(primaryRoleId))

                // ⭐️ 추출한 단일 Role ID를 position 필드에 매핑하여 프론트로 전송
                .position(primaryRoleId)

                // ⭐️ User 엔티티에 getStoreId()가 있다고 가정하고 매핑
                .storeId(user.getStoreId())
                .build();
    }

    // ⭐️ 역할의 우선순위를 결정하는 헬퍼 메서드 (예시)
    private static String getRoleNameForUI(String roleId) {
        switch (roleId) {
            case "ROLE_ADMIN": return "관리자";
            case "ROLE_MANAGER": return "점장";
            case "ROLE_CLERK": return "점원";
            default: return "";
        }
    }

    private static int getRoleOrder(String roleId) {
        switch (roleId) {
            case "ROLE_ADMIN": return 3;
            case "ROLE_MANAGER": return 2;
            case "ROLE_CLERK": return 1;
            default: return 0;
        }
    }
}