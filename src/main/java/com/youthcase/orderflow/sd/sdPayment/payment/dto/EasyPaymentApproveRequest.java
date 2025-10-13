package com.youthcase.orderflow.sd.sdPayment.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 카카오페이 / 토스페이 결제 승인(approve) 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EasyPaymentApproveRequest {
    private String orderId;   // 가맹점 주문 ID
    private String userId;    // 사용자 ID
    private String tid;       // ready API 응답에서 받은 거래 ID (카카오 TID)
    private String pgToken;   // approval_url 리다이렉트 시 전달된 PG TOKEN
    private Long Amount;
}