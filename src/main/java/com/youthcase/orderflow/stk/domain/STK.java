package com.youthcase.orderflow.stk.domain;

import com.youthcase.orderflow.pr.domain.Lot;
import com.youthcase.orderflow.pr.domain.Product;
import com.youthcase.orderflow.gr.domain.GR; // ✅ 임포트 경로 및 클래스명 수정
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "MM_STOCK",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_STOCK", columnNames = {"WAREHOUSE_ID", "GTIN", "LOT_ID"})
        }
)
public class STK {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_seq")
    @SequenceGenerator(name = "stock_seq", sequenceName = "SEQ_MM_STOCK", allocationSize = 1)
    @Column(name = "STK_ID")
    private Long stkId;

    @Column(name = "HAS_EXPIRATION_DATE", nullable = false)
    private Boolean hasExpirationDate;

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

    @Column(name = "LAST_UPDATED_AT")
    private LocalDateTime lastUpdatedAt;

    @Column(name = "STATUS", length = 20)
    private String status;

    // ============= FK 매핑 =============
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WAREHOUSE_ID", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GR_ID")
    private GR goodsReceipt; // ✅ 클래스명 GR로 변경

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GTIN", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOT_ID", nullable = false)
    private Lot lot;

    @Builder
    public STK(Boolean hasExpirationDate, Integer quantity, LocalDateTime lastUpdatedAt, String status, Warehouse warehouse, GR goodsReceipt, Product product, Lot lot) {
        this.hasExpirationDate = hasExpirationDate;
        this.quantity = quantity;
        this.lastUpdatedAt = lastUpdatedAt;
        this.status = status;
        this.warehouse = warehouse;
        this.goodsReceipt = goodsReceipt;
        this.product = product;
        this.lot = lot;
    }

    public void updateQuantity(Integer newQuantity) {
        this.quantity = newQuantity;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public void updateInfo(Integer quantity, String status, LocalDateTime lastUpdatedAt) {
        this.quantity = quantity;
        this.status = status;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public void markAsInactive() {
        this.status = "INACTIVE";
    }
}
