package com.youthcase.orderflow.sd.sdSales.domain;

import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "SALES_HEADER")
@SequenceGenerator(
        name = "sales_header_seq",
        sequenceName = "SALES_HEADER_SEQ",
        initialValue = 1,
        allocationSize = 1)
public class SalesHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sales_header_seq")
    private Long orderId;

    @ColumnDefault("sysdate")
    @Column(name= "SALES_DATE", insertable = false, updatable = false)
    private LocalDateTime salesDate;

    @Column(name= "TOTAL_AMOUNT", precision= 12, scale=2 , nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "order_no", unique = true, length = 20)
    private String orderNo;

    @Enumerated(EnumType.STRING)
    private SalesStatus salesStatus;

    /** ✅ 수정 포인트 #1: Set + SUBSELECT */
    @Builder.Default
    @OneToMany(mappedBy = "salesHeader", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<SalesItem> salesItems = new HashSet<>();

    /** ✅ 수정 포인트 #2: Set + SUBSELECT */
    @OneToMany(mappedBy = "salesHeader", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @Fetch(FetchMode.SUBSELECT)
    private Set<PaymentHeader> paymentHeaders = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store store;

    // ✅ 양방향 동기화 메서드
    public void addSalesItem(SalesItem item) {
        if (item == null) return;

        this.salesItems.add(item);
        item.setSalesHeader(this);

        // subtotal 자동 계산
        if (item.getSdPrice() != null && item.getSalesQuantity() > 0) {
            item.setSubtotal(item.getSdPrice().multiply(BigDecimal.valueOf(item.getSalesQuantity())));
        }
    }
}
