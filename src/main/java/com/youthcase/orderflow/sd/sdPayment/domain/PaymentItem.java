package com.youthcase.orderflow.sd.sdPayment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "PAYMENT_ITEM")
@SequenceGenerator(name = "payment_item_seq",
                    sequenceName = "PAYMENT_ITEM_SEQ",
                    allocationSize = 1)
public class PaymentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_item_seq")

    @Column(nullable = false)
    private Long paymentItemId; // 결제 아이템 고유번호

    @Column(length = 20, nullable = false)
    private String method; // 결제 수단 (CARD, CASH, EASY_PAYMENTS...)

    @Column(precision = 12, scale = 2)
    private BigDecimal amount; // 결제 금액

    @Column(length = 50)
    private String transactionNo; // 승인번호

    // FK 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYMENT_ID", nullable = false)
    private PaymentHeader paymentHeader;
}