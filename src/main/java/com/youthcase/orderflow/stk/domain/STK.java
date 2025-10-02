package com.youthcase.orderflow.stk.domain;

import com.youthcase.orderflow.pr.domain.Lot;
import com.youthcase.orderflow.pr.domain.Product;
import jakarta.persistence.*;
import lombok.AccessLevel; // 추가
import lombok.Builder;     // 추가
import lombok.Getter;      // @Data 대신 사용
import lombok.NoArgsConstructor; // 추가
import java.time.LocalDateTime;

// @Data 대신 @Getter, @NoArgsConstructor, @Builder 사용을 권장합니다.
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 1. 보호된 기본 생성자
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
    private Long stkId;   // 단일 PK (시퀀스 기반)

    @Column(name = "HAS_EXPIRATION_DATE", nullable = false)
    private Boolean hasExpirationDate;   // 유통기한 여부

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;   // 재고 수량

    @Column(name = "LAST_UPDATED_AT")
    private LocalDateTime lastUpdatedAt;   // 최종 업데이트 시간

    @Column(name = "STATUS", length = 20)
    private String status;   // 상태 (예: ACTIVE, INACTIVE)

    // ============= FK 매핑 =============
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WAREHOUSE_ID", nullable = false)
    private Warehouse warehouse;   // 창고

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GR_ID")
    private GoodsReceipt goodsReceipt;   // 입고 내역

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GTIN", nullable = false)
    private Product product;   // 상품

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOT_ID", nullable = false)
    private Lot lot;   // LOT 테이블 (제조일자/유통기한 포함)

    @Builder
    public STK(Boolean hasExpirationDate, Integer quantity, LocalDateTime lastUpdatedAt, String status, Warehouse warehouse, GoodsReceipt goodsReceipt, Product product, Lot lot) {
        this.hasExpirationDate = hasExpirationDate;
        this.quantity = quantity;
        this.lastUpdatedAt = lastUpdatedAt;
        this.status = status;
        this.warehouse = warehouse;
        this.goodsReceipt = goodsReceipt;
        this.product = product;
        this.lot = lot;
    }

    // 재고 수량 변경 메서드 등 비즈니스 메서드를 추가할 수 있습니다.
    public void updateQuantity(Integer newQuantity) {
        this.quantity = newQuantity;
        this.lastUpdatedAt = LocalDateTime.now();
    }
}
