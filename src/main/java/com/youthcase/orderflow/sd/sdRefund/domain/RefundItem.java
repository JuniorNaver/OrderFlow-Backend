package com.youthcase.orderflow.sd.sdRefund.domain;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "REFUND_ITEM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "refund_item_seq",
        sequenceName = "REFUND_ITEM_SEQ",
        allocationSize = 1
)
public class RefundItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refund_item_seq")
    @Column(name = "REFUND_ITEM_ID")
    private Long refundItemId;  // 환불 아이템 고유번호

    /** -----------------------------
     *  연관 관계 설정
     *  ----------------------------- */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REFUND_ID", nullable = false)
    private RefundHeader refundHeader;  // 어떤 환불 건(헤더)인지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYMENT_ITEM_ID", nullable = false)
    private PaymentItem paymentItem;  // ✅ 어떤 결제 아이템에 대한 환불인지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SALES_ITEM_ID", nullable = false)
    private SalesItem salesItem;  // ✅ 어떤 판매 상품인지 (조회용)

    /** -----------------------------
     *  금액 및 사유 정보
     *  ----------------------------- */

    @Column(name = "REFUND_AMOUNT", precision = 12, scale = 2, nullable = false)
    private BigDecimal refundAmount;  // ✅ 금액 타입 통일 (Double → BigDecimal)

    @Column(name = "REFUND_REASON", length = 255)
    private String refundReason;  // ✅ 길이 확장 (20 → 255)

    @Column(name = "TRANSACTION_NO", length = 50)
    private String transactionNo; // ✅ 카드/PG사 취소 승인번호 (부분 환불 구분용)
}