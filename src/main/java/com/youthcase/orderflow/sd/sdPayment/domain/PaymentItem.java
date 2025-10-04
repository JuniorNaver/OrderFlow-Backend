package com.youthcase.orderflow.sd.sdPayment.domain;

import com.youthcase.orderflow.sd.sdRefund.domain.RefundItem;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "PAYMENT_ITEM_ID", nullable = false)
    private Long paymentItemId; // 결제 아이템 고유번호

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_METHOD", length = 20, nullable = false)
    private PaymentMethod paymentMethod; // 결제 수단 (CARD, CASH, EASY_PAYMENTS...)

    @Column(name = "AMOUNT", precision = 12, scale = 2)
    private BigDecimal amount; // 결제 금액

    @Column(name = "TRANSACTION_NO", length = 50)
    private String transactionNo; // 카드사, 간편결제 승인번호(부분 결제용)

    // FK 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYMENT_ID", nullable = false)
    private PaymentHeader paymentHeader;

    // ✅ FK: 판매 아이템 (이 결제가 어떤 상품을 위한 것인지)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SALES_ITEM_ID", nullable = false)
    private SalesItem salesItem;

    // ✅ RefundItem 연관관계 (결제 아이템 하나에서 여러 환불 발생 가능)
    @OneToMany(mappedBy = "paymentItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefundItem> refundItems = new ArrayList<>();
}
