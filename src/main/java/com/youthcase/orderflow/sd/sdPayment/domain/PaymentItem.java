package com.youthcase.orderflow.sd.sdPayment.domain;

import com.youthcase.orderflow.sd.sdRefund.domain.RefundItem;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PAYMENT_ITEM")
@SequenceGenerator(name = "payment_item_seq",
        sequenceName = "PAYMENT_ITEM_SEQ",
        allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_item_seq")
    @Column(name = "PAYMENT_ITEM_ID")
    private Long paymentItemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_METHOD", length = 20, nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_STATUS", length = 20, nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "AMOUNT", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "TRANSACTION_NO", length = 50)
    private String transactionNo;

    @Column(name = "IMP_UID", length = 50)
    private String impUid; // 아임포트용


    // ✅ N:1 매핑 (결제 헤더)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYMENT_ID", nullable = false)
    private PaymentHeader paymentHeader;

    // ✅ ❌ 삭제하거나 nullable 로 변경
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SALES_ITEM_ID", nullable = true)
    private SalesItem salesItem;

    // ✅ 환불 내역
    @OneToMany(mappedBy = "paymentItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefundItem> refundItems = new ArrayList<>();


}
