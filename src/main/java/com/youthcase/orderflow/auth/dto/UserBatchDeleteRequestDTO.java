package com.youthcase.orderflow.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 관리자용 계정 일괄 삭제 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class UserBatchDeleteRequestDTO {

    @NotEmpty(message = "삭제할 사용자 ID 목록은 비어 있을 수 없습니다.")
    private List<String> userIds;
}
