package com.youthcase.orderflow.pr.domain;

import com.youthcase.orderflow.master.product.domain.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Entity
@Table(name = "SHOP_LIST")
@Getter
@Setter
public class ShopList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GTIN", referencedColumnName = "GTIN")
    private Product product;

    @Column(name="ORDERABLE", nullable=false)
    private Boolean orderable;

    @Column(name="DESCRIPTION", length=2000)
    private String description;

    // 스냅샷(저장 시점의 값 복사)
    @PositiveOrZero
    @Digits(integer = 12, fraction = 2)
    @Column(name="PURCHASE_PRICE_SNAPSHOT", precision=12, scale=2, nullable = false)
    private BigDecimal purchasePrice;

    @Column(name="CREATED_AT", updatable=false)
    private Instant createdAt;

    @Column(name="UPDATED_AT")
    private Instant updatedAt;

    // 저장 직전: 생성/수정 시각 + 금액 스케일 + orderable 기본값 보정
    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        updatedAt = createdAt;
        if (orderable == null) orderable = Boolean.TRUE;   // nullable=false 방어

        // 스냅샷 누락 시 현재 상품가로 채움
        if (purchasePrice == null && product != null && product.getPrice() != null) {
            purchasePrice = product.getPrice();
        }
        normalizePrices();
    }

    // 업데이트 직전: 수정 시각 + 금액 스케일 보정
    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
        normalizePrices();
    }

    // BigDecimal 스케일 강제 (소수 둘째 자리)
    private void normalizePrices() {
        if (purchasePrice != null) {
            purchasePrice = purchasePrice.setScale(2, RoundingMode.HALF_UP);
        }
    }

    // 필요시 세터에서 바로 스케일 강제하고 싶으면 아래처럼
    public void setPurchasePrice(BigDecimal price) {
        this.purchasePrice = (price == null) ? null : price.setScale(2, RoundingMode.HALF_UP);
    }
}
