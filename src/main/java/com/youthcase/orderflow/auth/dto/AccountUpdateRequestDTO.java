package com.youthcase.orderflow.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * 관리자용 계정 정보 및 상태 수정 요청 DTO
 * (비밀번호 변경은 별도 DTO 사용 권장)
 */
@Getter
@Setter
@NoArgsConstructor
public class AccountUpdateRequestDTO {

    // 기본 정보 (선택적 수정)
    private String name;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    private String workspace;

    private String storeId;

    // 계정 상태 정보 (관리자가 필수로 수정)
    @NotNull(message = "활성화 상태는 필수 입력값입니다.")
    private Boolean enabled;

    @NotNull(message = "잠금 상태는 필수 입력값입니다.")
    private Boolean locked;

    // 역할 목록 수정 (모든 역할을 새로 덮어쓰거나, 추가/제거 로직 필요)
    private Set<String> roleIds;
}
