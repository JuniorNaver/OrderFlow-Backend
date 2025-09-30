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
@Table(name = "payment_header")
@SequenceGenerator(
        name = "payment_header_seq",
        sequenceName = "PAYMENTS_HEADER_SEQ",
        allocationSize = 1)
public class PaymentHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_header_seq")
    private Long paymentId;

    @Column(insertable = false, updatable = false,
            columnDefinition = "DATE DEFAULT SYSDATE")
    private LocalDateTime requestedTime;

    @Column
    private LocalDateTime canceledTime; // 결제 취소 시각

    @Column(length = 50)
    private String transactionNo; // PG사 은행 거래번호

    @Column(name= "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount; // 총 결제금액 (VARCHAR2(200))

    @Column(length = 20, nullable = false)
    private String paymentStatus; // 결제 상태 (예: REQUESTED, APPROVED, CANCELED)

    @Column(nullable = false)
    private Long orderId; // 판매 고유번호(FK)

    // 연관관계 설정 (1:N)
    @OneToMany(mappedBy = "paymentHeader",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<PaymentItem> paymentItems;
}
