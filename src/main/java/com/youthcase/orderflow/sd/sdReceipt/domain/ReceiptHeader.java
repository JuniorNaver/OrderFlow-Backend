package com.youthcase.orderflow.sd.sdReceipt.domain;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SD_RECEIPT_HEADER")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ReceiptHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "receipt_header_seq")
    @SequenceGenerator(name = "receipt_header_seq", sequenceName = "SD_RECEIPT_HEADER_SEQ", allocationSize = 1)
    private Long receiptId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_id", foreignKey = @ForeignKey(name = "FK_RECEIPT_SALES"))
    private SalesHeader salesHeader;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false, foreignKey = @ForeignKey(name = "FK_RECEIPT_PAYMENT"))
    private PaymentHeader paymentHeader;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refund_id", foreignKey = @ForeignKey(name = "FK_RECEIPT_REFUND"))
    private RefundHeader refundHeader;

    @Column(name = "receipt_no", unique = true, nullable = false, length = 50)
    private String receiptNo;

    @Column(name = "total_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    @Builder.Default
    @OneToMany(mappedBy = "receiptHeader", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceiptItem> items = new ArrayList<>();

    public void setItems(List<ReceiptItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        this.items.forEach(i -> i.setReceiptHeader(this));
    }
}
