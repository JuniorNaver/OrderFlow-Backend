package com.youthcase.orderflow.sd.sdSales.domain;

import com.youthcase.orderflow.pr.domain.Product;
import com.youthcase.orderflow.stk.domain.STK;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "SALES_ITEM")
public class SalesItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sales_item_seq")
    @SequenceGenerator(
            name = "sales_item_seq",
            sequenceName = "SALES_ITEM_SEQ",
            allocationSize = 1)
    private Long no;

    @Column(name="PRODUCT_NAME", nullable = false)
    private String productName;

    @Column(name="QUANTITY", nullable = false)
    private int salesQuantity; //아이템 한개의 양

    @Column(name="SD_PRICE", precision= 12, scale=2 , nullable = false)
    private BigDecimal sdPrice;

    // N:1 매핑 (아이템 → 헤더 FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false)
    private SalesHeader salesHeader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STK_ID", nullable = false)
    private STK stk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GTIN", nullable = false)
    private Product product;

}