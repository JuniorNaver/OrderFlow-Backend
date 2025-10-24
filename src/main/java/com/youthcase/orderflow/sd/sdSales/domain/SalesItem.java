package com.youthcase.orderflow.sd.sdSales.domain;

import com.youthcase.orderflow.master.product.domain.Product;
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
            allocationSize = 1
    )
    private Long no;

    // ✅ 상품 단가
    @Column(name = "SD_PRICE", precision = 12, scale = 2, nullable = false)
    private BigDecimal sdPrice;

    // ✅ 판매 수량 (SalesQuantity)
    @Column(name = "SALES_QUANTITY", nullable = false)
    private Long salesQuantity;

    //한개 단위 상품 총액
    @Column(name = "SUBTOTAL", precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotal;

    // ✅ N:1 매핑 (아이템 → 헤더)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false)
    private SalesHeader salesHeader;

    // ✅ N:1 매핑 (상품)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GTIN", nullable = false)
    private Product product;

    // ✅ N:1 매핑 (재고)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STK_ID", nullable = true)
    private STK stk;
}
