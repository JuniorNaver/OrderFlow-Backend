package com.youthcase.orderflow.sd.sdRefund.domain;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REFUND_ID")
    private Long refundId;   // 환불 고유번호

    @Column(name = "REFUND_AMOUNT", nullable = false)
    private Double refundAmount;   // 환불 총액

    @Enumerated(EnumType.STRING)
    private RefundStatus refundStatus;   // REQUESTED, APPROVED, REJECTED

    @Column(name = "REQUESTED_TIME", nullable = false)
    private LocalDateTime requestedTime;   // 환불 요청 시간

    @Column(name = "APPROVED_TIME")
    private LocalDateTime approvedTime;    // 환불 승인 시간

    @ManyToOne(fetch = FetchType.LAZY) // ✅ 환불은 특정 결제 건에 대한 취소
    @JoinColumn(name = "PAYMENT_ID", nullable = false)
    private PaymentHeader paymentId;

    @Column(name = "REASON")
    private String reason; // 환불 사유

    // 연관관계: RefundItem 리스트
    @OneToMany(mappedBy = "refundHeader", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefundItem> refundItems;
}

