package com.youthcase.orderflow.auth.dto;

import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 사용자 상세 정보를 클라이언트에게 전달하기 위한 DTO.
 * (단일 Role 기반 구조에 맞게 수정됨)
 */
@Getter
@Builder
public class UserResponseDTO {

    private String userId;
    private String email;
    private String name;
    private LocalDateTime createdAt;
    private boolean enabled;

    // 프론트엔드 AccountManage.jsx 테이블의 '직책' 열 (Role 이름)
    private String position;

    // 프론트엔드 AccountManage.jsx 테이블의 '점포 ID' 열
    private String storeId;

    // 추가적으로 Role ID도 함께 반환 (API 단일화 목적)
    private String roleId;

    /**
     * User 엔티티 → UserResponseDTO 변환
     */
    public static UserResponseDTO from(User user) {

        // ✅ Role이 존재할 때만 값 추출 (null-safe)
        Role role = user.getRole();
        String roleId = (role != null) ? role.getRoleId() : null;
        String positionName = getRoleNameForUI(roleId);

        // ✅ Store ID (nullable)
        String storeId = (user.getStore() != null)
                ? user.getStore().getStoreId()
                : null;

        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .createdAt(user.getCreatedAt())
                .enabled(user.isEnabled())
                .position(positionName)
                .storeId(storeId)
                .roleId(roleId)
                .build();
    }

    /**
     * 역할 ID → UI 표시용 한글명 변환
     */
    private static String getRoleNameForUI(String roleId) {
        if (roleId == null) return "";
        switch (roleId) {
            case "ADMIN": case "ROLE_ADMIN": return "관리자";
            case "MANAGER": case "ROLE_MANAGER": return "점장";
            case "CLERK": case "ROLE_CLERK": return "점원";
            default: return "";
        }
    }
}
