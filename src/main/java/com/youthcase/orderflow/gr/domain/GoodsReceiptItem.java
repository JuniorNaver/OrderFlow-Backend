package com.youthcase.orderflow.gr.domain;

import com.youthcase.orderflow.gr.status.GRExpiryType;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GR_ITEM")
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
            sequenceName = "GR_ITEM_SEQ",
            allocationSize = 1
    )
    @Column(name = "ITEM_NO")
    private Long itemNo;  // ITEM_NO (PK)

    @Builder.Default
    @Column(name = "QTY", nullable = false)
    private Long qty = 0L;  // 수량

    @Column(name = "NOTE", length = 255)
    private String note; // 비고

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GR_HEADER_ID", nullable = false)
    private GoodsReceiptHeader header;

    // ✅ FK: 창고 (WAREHOUSE_MASTER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WAREHOUSE_ID", nullable = false)
    private Warehouse warehouse;


    // ✅ FK: 상품 (PRODUCT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GTIN", referencedColumnName = "GTIN", nullable = false)
    private Product product;

    @OneToMany(mappedBy = "goodsReceiptItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Lot> lots = new ArrayList<>();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "EXPIRY_CALC_TYPE", nullable = false)
    private GRExpiryType expiryCalcType = GRExpiryType.MFG_BASED;

    @Column(name = "MFG_DATE")
    private LocalDate mfgDate;

    @Column(name = "EXP_DATE_MANUAL")
    private LocalDate expDateManual;

    /**
     * LOT 수량 자동 합계 업데이트
     */
    public void updateQtyFromLots() {
        this.qty = this.lots.stream()
                .mapToLong(Lot::getQty)
                .sum();
    }

    /**
     * LOT 추가 메서드 (연관관계 편의)
     */
    public void addLot(Lot lot) {
        this.lots.add(lot);
        lot.setGoodsReceiptItem(this);
        updateQtyFromLots();
    }
}

