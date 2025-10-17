package com.youthcase.orderflow.sd.sdPayment.dto;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 💳 PaymentRequest (통합 버전)
 * - 단일 결제와 혼합 결제를 모두 지원
 * - 단일 결제 시: paymentMethod + amount 사용
 * - 혼합 결제 시: splits(List<PaymentSplit>) 사용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    private Long orderId;                // 주문번호
    private BigDecimal totalAmount;      // 전체 결제 금액 (혼합 결제 시 총합)

    // ✅ 단일 결제 전용
    private PaymentMethod paymentMethod; // 결제 수단 (CARD, CASH, EASY)
    private BigDecimal amount;           // 결제 금액

    // ✅ 혼합 결제 전용
    private List<PaymentSplit> splits;   // 분할 결제 내역 (CARD+현금 등)

    // ✅ 선택적 PG 데이터
    private String transactionNo;        // 카드사 승인번호 등
    private String impUid;               // 아임포트 결제 고유 ID
    private String merchantUid;          // 가맹점 주문번호
}
