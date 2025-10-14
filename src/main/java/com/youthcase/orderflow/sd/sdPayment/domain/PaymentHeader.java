package com.youthcase.orderflow.sd.sdPayment.domain;

import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Column(name = "REQUESTED_TIME",updatable = false)
    private LocalDateTime requestedTime;

    @Column(name = "CANCELED_TIME")
    private LocalDateTime canceledTime; // 결제 취소 시각


    @Column(name= "TOTAL_AMOUNT", precision = 12, scale = 2)
    private BigDecimal totalAmount; // 총 결제금액 (NUMBER(12,2))

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_STATUS", length = 20, nullable = false)
    private PaymentStatus paymentStatus; // 결제 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_PAYMENT_ORDER"))
    private SalesHeader salesHeader;

    // 연관관계 설정 (1:N)
    @OneToMany(mappedBy = "paymentHeader",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<PaymentItem> paymentItems;
}
