package com.youthcase.orderflow.sd.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sales_item")
public class SalesItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sales_item_seq")
    @SequenceGenerator(
            name = "sales_item_seq",
            sequenceName = "SALES_ITEM_SEQ",
            allocationSize = 1)
    private Long no;

    @Column(nullable = false)
    private int quantity;

    @Column(precision = 10, scale = 2, nullable = false)
    private int sdPrice;

    // N:1 매핑 (아이템 → 헤더 FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private SalesHeader salesHeader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STK_ID", nullable = false)
    private MmStock mmstock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gtin", nullable = false)
    private ProductMaster product;

}