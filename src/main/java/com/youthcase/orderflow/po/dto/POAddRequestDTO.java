package com.youthcase.orderflow.po.dto;

import lombok.*;

/**
 * 📥 POAddRequest
 * - 사용자 정보 + 상품 정보를 묶어서 받는 Wrapper DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class POAddRequestDTO {
    private String userId;             // 로그인 사용자 ID
    private POItemRequestDTO item;     // 추가할 상품 정보
}
