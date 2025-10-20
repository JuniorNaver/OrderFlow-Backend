package com.youthcase.orderflow.sd.sdPayment.domain;

import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @Column(name = "REQUESTED_TIME", updatable = false)
    private LocalDateTime requestedTime;

    @Column(name = "CANCELED_TIME")
    private LocalDateTime canceledTime;

    @Column(name= "TOTAL_AMOUNT", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_STATUS", length = 20, nullable = false)
    private PaymentStatus paymentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_PAYMENT_ORDER"))
    private SalesHeader salesHeader;

    /** ✅ 수정 포인트: Set + SUBSELECT */
    @Builder.Default
    @OneToMany(mappedBy = "paymentHeader", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<PaymentItem> paymentItems = new HashSet<>();
}
