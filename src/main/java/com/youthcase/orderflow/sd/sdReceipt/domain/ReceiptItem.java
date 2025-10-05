package com.youthcase.orderflow.sd.sdReceipt.domain;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "SD_RECEIPT_ITEM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "receipt_item_seq")
    @SequenceGenerator(name = "receipt_item_seq", sequenceName = "SD_RECEIPT_ITEM_SEQ", allocationSize = 1)
    private Long receiptItemId;

    @ManyToOne
    @JoinColumn(name = "receipt_id")
    private ReceiptHeader receiptHeader;

    // ✅ 판매 아이템 직접 참조
    @ManyToOne
    @JoinColumn(name = "sales_item_id")
    private SalesItem salesItem;

    // ✅ 결제 아이템 직접 참조
    @ManyToOne
    @JoinColumn(name = "payment_item_id")
    private PaymentItem paymentItem;
}

