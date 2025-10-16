package com.youthcase.orderflow.sd.sdRefund.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequest {
    private Long paymentId;            // ✅ 환불은 Payment 기준
    private String reason;             // 환불 사유
    private List<RefundItemResponse> items;// 환불 상품 목록
}
