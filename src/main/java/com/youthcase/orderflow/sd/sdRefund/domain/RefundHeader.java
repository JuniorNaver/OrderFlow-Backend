package com.youthcase.orderflow.sd.sdRefund.domain;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "REFUND_HEADER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "refund_header_seq",
        sequenceName = "REFUND_HEADER_SEQ",
        allocationSize = 1
)
public class RefundHeader {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refund_header_seq")
    @Column(name = "REFUND_ID")
    private Long refundId;   // 환불 고유번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYMENT_ID", nullable = false)
    private PaymentHeader paymentHeader; // ✅ 결제 헤더 참조 (명확한 명명)

    @Column(name = "REFUND_AMOUNT", precision = 12, scale = 2, nullable = false)
    private BigDecimal refundAmount;   // ✅ 환불 총액 (금액 타입 일관화)

    @Enumerated(EnumType.STRING)
    @Column(name = "REFUND_STATUS", length = 20, nullable = false)
    private RefundStatus refundStatus;   // REQUESTED, APPROVED, REJECTED, COMPLETED 등

    @Column(name = "REQUESTED_TIME",
            insertable = false, updatable = false,
            columnDefinition = "DATE DEFAULT SYSDATE")
    private LocalDateTime requestedTime;   // ✅ 기본값 SYSDATE (DB 자동)

    @Column(name = "APPROVED_TIME")
    private LocalDateTime approvedTime;    // 환불 승인 시간

    @Column(name = "CANCELED_TIME")
    private LocalDateTime canceledTime;    // 환불 취소 시간 (필요 시)

    @Column(name = "REASON", length = 255)
    private String reason; // 환불 사유

    @OneToMany(mappedBy = "refundHeader",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    
    @Builder.Default
    private List<RefundItem> refundItems = new ArrayList<>();

}