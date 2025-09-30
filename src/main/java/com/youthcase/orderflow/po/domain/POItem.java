package com.youthcase.orderflow.po.domain;

import com.youthcase.orderflow.pr.domain.Product;
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
@ToString(exclude = {"poHeader", "product"})
public class POItem {

    // 발주 아이템 고유 ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "po_item_seq_gen")
    @SequenceGenerator(
            name = "po_item_seq_gen",
            sequenceName = "PO_ITEM_SEQ",  // 오라클 시퀀스 이름
            allocationSize = 1
    )
    @Column(name = "ITEM_NO", nullable = false)
    private Long itemNo;

    // 예상 도착 일자
    @Column(name = "EXPECTED_ARRIVAL")
    private LocalDate expectedArrival;

    // 매입 단가
    @Column(name = "UNIT_PRICE", nullable = false)
    private Long unitPrice;

    // 발주 수량
    @Column(name = "ORDER_QTY", nullable = false)
    private Long orderQty;

    // 출고되지 않은 수량
    @Column(name = "PENDING_QTY")
    private Long pendingQty;

    // 실제 출고된 수량
    @Column(name = "SHIPPED_QTY")
    private Long shippedQty;

    // 발주내역 ID (FK → PO_HEADER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PO_ID", nullable = false)
    private POHeader poHeader;

    // 상품 고유 코드 (FK → PRODUCT 같은 테이블이라고 가정)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GTIN", nullable = false)
    private Product product;

}
