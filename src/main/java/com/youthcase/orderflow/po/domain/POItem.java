package com.youthcase.orderflow.po.domain;

import com.youthcase.orderflow.master.price.domain.Price;
import com.youthcase.orderflow.master.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "PO_ITEM")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"poHeader", "gtin"})
public class POItem {

    // 발주 아이템 고유 ID (PK)
    @Id
    @Column(name = "ITEM_NO", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "po_item_seq_gen")
    @SequenceGenerator(
            name = "po_item_seq_gen",
            sequenceName = "PO_ITEM_SEQ",  // 오라클 시퀀스 이름
            allocationSize = 1
    )
    private Long itemNo;

    // 예상 도착 일자
    @Column(name = "EXPECTED_ARRIVAL")
    private LocalDate expectedArrival;

    // 매입 단가
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PURCHASE_PRICE", nullable = false)
    private Price price;

    // 발주 수량
    @Column(name = "ORDER_QTY", nullable = false)
    private Long orderQty;

    // 미출 수량
    @Column(name = "PENDING_QTY")
    private Long pendingQty;

    // 출고 수량
    @Column(name = "SHIPPED_QTY")
    private Long shippedQty;

    // 합계
    @Column(name = "TOTAL")
    private Long total;

    // 발주내역 ID (FK → PO_HEADER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PO_ID", nullable = false)
    private POHeader poHeader;

    // 상품 고유 코드 (FK → PRODUCT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GTIN", nullable = false)
    private Product gtin;

    @Enumerated(EnumType.STRING)
    private POStatus status;

}
