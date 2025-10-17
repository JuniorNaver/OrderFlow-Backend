package com.youthcase.orderflow.gr.domain;

import com.youthcase.orderflow.master.domain.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MM_GR_ITEM")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GoodsReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gr_item_seq_gen")
    @SequenceGenerator(
            name = "gr_item_seq_gen",
            sequenceName = "MM_GR_ITEM_SEQ",
            allocationSize = 1
    )
    @Column(name = "ITEM_NO")
    private Long itemNo;  // ITEM_NO (PK)

    @Column(name = "QTY", nullable = false)
    private Integer qty;  // 수량

    @Column(name = "NOTE", length = 255)
    private String note; // 비고

    // ✅ FK: 입고헤더 (MM_GR_HEADER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GR_HEADER_ID", nullable = false)
    private GoodsReceiptHeader header;

    // ✅ FK: 상품 (PRODUCT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GTIN", referencedColumnName = "GTIN", nullable = false)
    private Product product;
}
