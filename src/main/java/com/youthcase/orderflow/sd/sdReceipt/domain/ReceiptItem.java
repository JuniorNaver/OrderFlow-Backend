package com.youthcase.orderflow.sd.sdReceipt.domain;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "SD_RECEIPT_ITEM")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "receipt_item_seq")
    @SequenceGenerator(name = "receipt_item_seq", sequenceName = "SD_RECEIPT_ITEM_SEQ", allocationSize = 1)
    private Long receiptItemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receipt_id", nullable = false, foreignKey = @ForeignKey(name = "FK_RECEIPT_ITEM_HEADER"))
    private ReceiptHeader receiptHeader;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sales_item_id", nullable = false, foreignKey = @ForeignKey(name = "FK_RECEIPT_ITEM_SALESITEM"))
    private SalesItem salesItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_item_id", foreignKey = @ForeignKey(name = "FK_RECEIPT_ITEM_PAYMENTITEM"))
    private PaymentItem paymentItem;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name="unit_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name="total_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalPrice;
}

