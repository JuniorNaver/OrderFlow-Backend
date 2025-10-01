package com.youthcase.orderflow.sd.sdRefund.domain;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SD_REFUND_ITEM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REFUND_ITEM_ID")
    private Long refundItemId;  // 환불 아이템 고유번호

    @Column(name = "ORDER_ITEM_ID", nullable = false)
    private Long orderItemId;   // 어떤 판매 아이템(OrderItem)인지

    @Column(name = "REFUND_AMOUNT", nullable = false)
    private Double refundAmount;  // 환불 금액

    @Column(name = "REFUND_REASON", length = 20, nullable = false)
    private String refundReason;  // 환불 사유

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REFUND_ID", nullable = false)
    private RefundHeader refundHeader;  // 어떤 환불 건(헤더)인지 연결

}