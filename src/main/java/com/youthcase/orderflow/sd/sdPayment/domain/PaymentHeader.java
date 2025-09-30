package com.youthcase.orderflow.sd.sdPayment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "PAYMENT_HEADER")
@SequenceGenerator(
        name = "payment_header_seq",
        sequenceName = "PAYMENTS_HEADER_SEQ",
        allocationSize = 1)
public class PaymentHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_header_seq")
    @Column(name = "PAYMENT_ID", nullable = false)
    private Long paymentId;

    @Column(name = "REQUESTED_TIME",
            insertable = false, updatable = false,
            columnDefinition = "DATE DEFAULT SYSDATE")
    private LocalDateTime requestedTime;

    @Column(name = "CANCELED_TIME")
    private LocalDateTime canceledTime; // 결제 취소 시각

    @Column(name = "TRANSACTION_NO", length = 50)
    private String transactionNo; // PG사 은행 거래번호

    @Column(name= "TOTAL_AMOUNT", precision = 12, scale = 2)
    private BigDecimal totalAmount; // 총 결제금액 (VARCHAR2(200))

    @Column(name = "PAYMENT_STATUS", length = 20, nullable = false)
    private String paymentStatus; // 결제 상태 (예: REQUESTED, APPROVED, CANCELED)

    @Column(name = "ORDER_ID", nullable = false)
    private Long orderId; // 판매 고유번호(FK)

    // 연관관계 설정 (1:N)
    @OneToMany(mappedBy = "paymentHeader",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<PaymentItem> paymentItems;
}
