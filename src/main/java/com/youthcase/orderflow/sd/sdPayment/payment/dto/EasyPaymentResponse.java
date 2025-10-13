package com.youthcase.orderflow.sd.sdPayment.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 외부 PG(카카오페이, 토스페이먼츠) API 응답 전용 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EasyPaymentResponse {
    private boolean success;       // 요청 성공 여부
    private String message;        // 응답 메시지
    private String redirectUrl;    // 결제 페이지 이동 URL
    private String transactionId;  // TID (카카오) or paymentKey (토스)
    private boolean approved;      // 승인 여부 (approve 단계에서 true)
}
