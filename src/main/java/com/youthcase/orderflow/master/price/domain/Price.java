package com.youthcase.orderflow.master.price.domain;

import com.youthcase.orderflow.master.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

/**
 * 📦 PRICE_MASTER
 * 상품별 매입/매출 단가 관리 (Product 확장 테이블)
 *
 * 🔸 설계 개념
 * - Product.GTIN을 공유 PK로 사용 (Shared Primary Key)
 * - Product 삭제 시 PriceMaster 자동 삭제 (OnDelete = CASCADE)
 * - 기준단가(Product.price)는 Product 엔티티에서 관리
 *
 * ------------------------------------------------------------
 * 💾 예상 DDL (Oracle)
 * ------------------------------------------------------------
 * CREATE TABLE PRICE_MASTER (
 *     GTIN            VARCHAR2(14)    PRIMARY KEY,
 *     PURCHASE_PRICE  NUMBER(12,2)    NOT NULL,
 *     SALE_PRICE      NUMBER(12,2)    NOT NULL,
 *     CONSTRAINT FK_PRICE_PRODUCT
 *         FOREIGN KEY (GTIN)
 *         REFERENCES PRODUCT (GTIN)
 *         ON DELETE CASCADE
 * );
 *
 * 💡 설명:
 * - PK = FK: PRODUCT.GTIN을 그대로 PRICE_MASTER.GTIN으로 사용
 * - Product 삭제 시 종속된 PRICE_MASTER 자동 삭제
 * - BigDecimal(12,2) 스케일은 Product.price와 동일하게 유지
 * ------------------------------------------------------------
 */
@Entity
@Table(name = "PRICE_MASTER")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Price {

    /** 🔹 PK = FK (Product.GTIN 공유) */
    @Id
    @Column(name = "GTIN", length = 14, nullable = false)
    private String gtin;

    /** 🔹 Product와 1:1 (공유 PK 기반) */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // ✅ Product의 PK(GTIN)를 Price의 PK로 공유
    @JoinColumn(name = "GTIN")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    /** 🔹 매입단가 */
    @Column(name = "PURCHASE_PRICE", precision = 12, scale = 2, nullable = false)
    @Comment("매입 단가 (본사가 공급사로부터 매입하는 가격)")
    private BigDecimal purchasePrice;

    /** 🔹 매출단가 */
    @Column(name = "SALE_PRICE", precision = 12, scale = 2, nullable = false)
    @Comment("매출 단가 (가맹점/소비자에게 판매하는 가격)")
    private BigDecimal salePrice;

    // ---------------------------------------------------------
    // ✅ 금액 스케일 보정 로직 (Product.price setter와 일관성 유지)
    // ---------------------------------------------------------
    public void setPurchasePrice(BigDecimal price) {
        this.purchasePrice = (price == null)
                ? null
                : price.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public void setSalePrice(BigDecimal price) {
        this.salePrice = (price == null)
                ? null
                : price.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    @PrePersist
    @PreUpdate
    private void normalizePrice() {
        if (purchasePrice != null) {
            purchasePrice = purchasePrice.setScale(2, java.math.RoundingMode.HALF_UP);
        }
        if (salePrice != null) {
            salePrice = salePrice.setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }
}
