package com.youthcase.orderflow.stk.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import com.youthcase.orderflow.pr.domain.Lot;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.gr.domain.GoodsReceiptHeader;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter // 👈 ⭐️ 이 어노테이션을 추가합니다!
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "MM_STOCK",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_STOCK", columnNames = {"WAREHOUSE_ID", "GR_HEADER_ID", "LOT_ID"})
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
    // ⭐️ WAREHOUSE_MASTER 엔티티를 참조하는 필드 (WarehouseId를 기반으로 추정)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WAREHOUSE_ID", referencedColumnName = "WAREHOUSE_ID")
    @JsonIgnore // ⭐️ 이 필드를 JSON 변환 시 무시하도록 설정
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GR_HEADER_ID")
    private GoodsReceiptHeader goodsReceipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GTIN", nullable = false)
    private Product product;

    // ⭐️ LOT 엔티티를 참조하는 필드 (LotId를 기반으로 추정)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOT_ID", referencedColumnName = "LOT_ID")
    @JsonIgnore // ⭐️ 이 필드를 JSON 변환 시 무시하도록 설정
    private Lot lot;

    @Builder
    public STK(Boolean hasExpirationDate, Integer quantity, LocalDateTime lastUpdatedAt, String status, Warehouse warehouse, GoodsReceiptHeader goodsReceipt, Product product, Lot lot) {
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

    // 3. 폐기 수량 감소 메서드 추가
    public void deductForDisposal(Integer amountToDeduct) {
        if (amountToDeduct == null || amountToDeduct <= 0) {
            throw new IllegalArgumentException("폐기 수량은 0보다 커야 합니다.");
        }
        if (this.quantity < amountToDeduct) {
            // 폐기할 수량이 현재 재고 수량보다 많으면 오류 발생 (전량 폐기 로직 필요 시 수정 가능)
            throw new IllegalArgumentException("폐기할 재고 수량이 현재 재고보다 많습니다.");
        }

        this.quantity -= amountToDeduct;
        this.lastUpdatedAt = LocalDateTime.now();

        // 수량이 0이 되면 상태를 DISPOSED로 변경합니다.
        if (this.quantity == 0) {
            this.status = "DISPOSED";
        }
    }

    /**
     * 재고 상태만 갱신합니다.
     * @param newStatus 새로운 상태 (예: 'NEAR_EXPIRY', 'DISPOSED', 'INACTIVE')
     */
    public void updateStatus(String newStatus) {
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("새로운 재고 상태는 필수입니다.");
        }
        this.status = newStatus;
        this.lastUpdatedAt = java.time.LocalDateTime.now();
    }

    // ⭐️ 위치 변경 필요 여부를 나타내는 필드를 추가합니다.
    private Boolean isRelocationNeeded;
}

