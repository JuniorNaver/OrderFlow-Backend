package com.youthcase.orderflow.po.dto;

import lombok.*;

/**
 * 📤 POHeaderRequestDTO
 * - 장바구니 저장 시 비고(remarks) 전달용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class POHeaderRequestDTO {
    /**
     * 발주를 생성, 수정하는 사용자 ID (로그인 사용자)
     */
    private String userId;

    /**
     * 비고(Optional) - 사용자가 입력한 메모나 요청사항
     */
    private String remarks;
}
