package com.youthcase.orderflow.sd.sdReceipt.domain;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "SD_RECEIPT_HEADER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "receipt_header_seq")
    @SequenceGenerator(name = "receipt_header_seq", sequenceName = "SD_RECEIPT_HEADER_SEQ", allocationSize = 1)
    private Long receiptId;

    // ✅ 판매 헤더와 연결
    @OneToOne
    @JoinColumn(name = "sales_id")
    private SalesHeader salesHeader;

    // ✅ 결제 헤더와 연결
    @OneToOne
    @JoinColumn(name = "payment_id")
    private PaymentHeader paymentHeader;

    // ✅ 환불 헤더 (선택적)
    @OneToOne(optional = true)
    @JoinColumn(name = "refund_id")
    private RefundHeader refundHeader;

    private LocalDateTime receiptDate;
    private String storeName;
    private BigDecimal totalAmount;

    // ✅ 영수증 아이템 목록 (여기 없어서 빨간줄 뜬 거야)
    @OneToMany(mappedBy = "receiptHeader", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceiptItem> items;

    // ✅ 필요하면 수동 세터로 연관관계 유지
    public void setItems(List<ReceiptItem> items) {
        this.items = items;
        if (items != null) {
            items.forEach(i -> i.setReceiptHeader(this));
        }
    }

    }


