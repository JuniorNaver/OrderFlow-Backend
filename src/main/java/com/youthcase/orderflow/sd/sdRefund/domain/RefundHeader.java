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
    private Long refundId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYMENT_ID", nullable = false)
    private PaymentHeader paymentHeader;

    @Column(name = "REFUND_AMOUNT", precision = 12, scale = 2, nullable = false)
    private BigDecimal refundAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "REFUND_STATUS", length = 20, nullable = false)
    private RefundStatus refundStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "REFUND_REASON", length = 30, nullable = false)
    private RefundReason refundReason;  // ✅ Enum 기반 환불 사유

    @Column(name = "DETAIL_REASON", length = 255)
    private String detailReason;        // ✅ “기타” 또는 세부사유

    @Column(name = "REQUESTED_TIME",
            insertable = false, updatable = false,
            columnDefinition = "DATE DEFAULT SYSDATE")
    private LocalDateTime requestedTime;

    @Column(name = "APPROVED_TIME")
    private LocalDateTime approvedTime;

    @Column(name = "CANCELED_TIME")
    private LocalDateTime canceledTime;

    @OneToMany(mappedBy = "refundHeader", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RefundItem> refundItems = new ArrayList<>();
}
