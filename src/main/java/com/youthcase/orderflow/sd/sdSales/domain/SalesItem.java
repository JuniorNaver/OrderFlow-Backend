package com.youthcase.orderflow.sd.sdSales.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "SALES_ITEM")
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
    private BigDecimal sdPrice;

    // N:1 매핑 (아이템 → 헤더 FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false)
    private SalesHeader salesHeader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STK_ID", nullable = false)
    private MmStock mmstock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GTIN", nullable = false)
    private ProductMaster product;

}