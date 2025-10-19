package com.youthcase.orderflow.sd.sdReceipt.domain;

import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "SD_RECEIPT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "receipt_seq")
    @SequenceGenerator(name = "receipt_seq", sequenceName = "SD_RECEIPT_SEQ", allocationSize = 1)
    private Long receiptId;

    @Column(name = "receipt_no", unique = true, nullable = false, length = 50)
    private String receiptNo; // 영수증 번호

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt; // 발행 일시

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_id")
    private SalesHeader salesHeader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private PaymentHeader paymentHeader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refund_id")
    private RefundHeader refundHeader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store; // ✅ 지점 정보

    @PrePersist
    void prePersist() {
        if (issuedAt == null) issuedAt = LocalDateTime.now();
        if (receiptNo == null || receiptNo.isBlank()) {
            this.receiptNo = "R" + System.currentTimeMillis();
        }
    }
}