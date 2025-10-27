package com.youthcase.orderflow.stk.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import com.youthcase.orderflow.gr.domain.Lot;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.gr.domain.GoodsReceiptHeader;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter // ⭐️ 모든 필드에 대한 Getter/Setter를 제공하여 서비스 코드 오류를 해결합니다.
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
    private Long quantity; // ⭐️ setQuantity/getQuantity는 @Getter/@Setter가 처리합니다.

    @Column(name = "LAST_UPDATED_AT")
    private LocalDateTime lastUpdatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 30, nullable = false)
    private StockStatus status;

    // ⭐️ 위치 변경 필요 여부 필드
    @Column(name = "IS_RELOCATION_NEEDED")
    private Boolean isRelocationNeeded = false;

    // ⭐️ STKServiceImpl에서 사용된 location 필드 추가 (재고가 속한 위치를 나타내는 별도 String 필드)
    // 실제로는 Warehouse 엔티티나 Location 엔티티를 참조해야 하지만, 현재 오류 해결을 위해 String으로 추가합니다.
    @Column(name = "LOCATION_CODE", length = 50)
    private String location;

    // ============= FK 매핑 =============
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WAREHOUSE_ID", referencedColumnName = "WAREHOUSE_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GR_HEADER_ID")
    private GoodsReceiptHeader goodsReceipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GTIN", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOT_ID", referencedColumnName = "LOT_ID")
    @JsonIgnore
    private Lot lot;

    @Builder
    public STK(Boolean hasExpirationDate, Long quantity, LocalDateTime lastUpdatedAt, StockStatus status,
               Warehouse warehouse, GoodsReceiptHeader goodsReceipt, Product product, Lot lot,
               Boolean isRelocationNeeded, String location) {
        this.hasExpirationDate = hasExpirationDate;
        this.quantity = quantity;
        this.lastUpdatedAt = lastUpdatedAt;
        this.status = status;
        this.warehouse = warehouse;
        this.goodsReceipt = goodsReceipt;
        this.product = product;
        this.lot = lot;
        this.isRelocationNeeded = isRelocationNeeded;
        this.location = location; // 빌더에 location 추가
    }

    // --------------------------------------------------
    // ⭐️ Service에서 사용된 위임(Delegate) Getter 메서드 추가
    // --------------------------------------------------

    /**
     * 1. STKServiceImpl에서 사용된 getProductName()을 Product 엔티티로 위임
     */
    public String getProductName() {
        return this.product != null ? this.product.getProductName() : null;
    }

    /**
     * 2. STKServiceImpl에서 사용된 getExpiryDate()를 Lot 엔티티로 위임
     */
    public LocalDate getExpiryDate() {
        // Lot 엔티티에 expDate 필드가 LocalDate 타입으로 정의되어 있다고 가정
        return this.lot != null ? this.lot.getExpDate() : null;
    }

    // --------------------------------------------------
    // 📦 비즈니스 로직 메서드
    // --------------------------------------------------

    public void updateQuantity(Long newQuantity) {
        this.quantity = newQuantity;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    // ✅ status를 enum 기반으로 변경
    public void updateInfo(Long quantity, StockStatus status, LocalDateTime lastUpdatedAt) {
        this.quantity = quantity;
        this.status = status;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public void markAsInactive() {
        this.status = StockStatus.INACTIVE;
    }

    public void deductForDisposal(Long amountToDeduct) {
        if (amountToDeduct == null || amountToDeduct <= 0) {
            throw new IllegalArgumentException("폐기 수량은 0보다 커야 합니다.");
        }
        if (this.quantity < amountToDeduct) {
            throw new IllegalArgumentException("폐기할 재고 수량이 현재 재고보다 많습니다.");
        }

        this.quantity -= amountToDeduct;
        this.lastUpdatedAt = LocalDateTime.now();

        if (this.quantity == 0) {
            this.status = StockStatus.DISPOSED;
        }
    }

    // ✅ 문자열 대신 enum 기반으로 변경
    public void updateStatus(StockStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("새로운 재고 상태는 필수입니다.");
        }
        this.status = newStatus;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public static STK createForRefund(Product product, Lot lot, Long quantity) {
        STK stk = new STK();
        stk.setProduct(product);
        stk.setLot(lot);
        stk.setQuantity(quantity);
        stk.setHasExpirationDate(lot != null && lot.getExpDate() != null);
        stk.setStatus(StockStatus.RETURNED);
        stk.setLastUpdatedAt(LocalDateTime.now());
        return stk;
    }
}
