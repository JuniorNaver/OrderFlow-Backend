package com.youthcase.orderflow.sd.sdPayment.dto;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 💳 결제 헤더 응답 DTO
 * - 결제 단위(1회 주문)에 대한 전체 요약
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long paymentId;          // 결제 PK
    private Long orderId;            // 주문 PK
    private BigDecimal totalAmount;  // 총 결제 금액
    private PaymentStatus status;    // 결제 상태 (PAID, CANCELED 등)
    private LocalDateTime requestedTime;
    private LocalDateTime canceledTime; // null 가능
    private List<PaymentItemResponse> items; // 결제 항목 리스트

    // ✅ PaymentHeader → PaymentResponse 변환
    public static PaymentResponse from(PaymentHeader header) {
        return PaymentResponse.builder()
                .paymentId(header.getPaymentId())
                .orderId(header.getSalesHeader().getOrderId())
                .totalAmount(header.getTotalAmount())
                .status(header.getPaymentStatus())
                .requestedTime(header.getRequestedTime())
                .canceledTime(header.getCanceledTime())
                .items(header.getPaymentItems() != null
                        ? header.getPaymentItems()
                        .stream()
                        .map(PaymentItemResponse::from)
                        .collect(Collectors.toList())
                        : List.of())
                .build();
    }
}