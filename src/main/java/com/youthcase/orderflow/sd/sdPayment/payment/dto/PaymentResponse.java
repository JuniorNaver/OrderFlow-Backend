package com.youthcase.orderflow.sd.sdPayment.payment.dto;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long paymentId;          // 결제 PK
    private Long orderId;            // 주문 PK
    private BigDecimal totalAmount;  // 총 결제 금액
    private PaymentStatus status;    // 결제 상태
    private LocalDateTime requestedTime;
    private LocalDateTime canceledTime;
    private List<PaymentItemResponse> items; // 상세 항목 리스트

    // ✅ PaymentHeader -> PaymentResponse 변환
    public static PaymentResponse from(PaymentHeader header) {
        return PaymentResponse.builder()
                .paymentId(header.getPaymentId())
                .orderId(header.getSalesHeader().getOrderId())
                .totalAmount(header.getTotalAmount())
                .status(header.getPaymentStatus())
                .requestedTime(header.getRequestedTime())
                .canceledTime(header.getCanceledTime())
                .items(header.getPaymentItems()
                        .stream()
                        .map(PaymentItemResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }


}
