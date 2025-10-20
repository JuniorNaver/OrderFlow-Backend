package com.youthcase.orderflow.master.price.domain;

import com.youthcase.orderflow.master.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "PRICE_MASTER")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Price {

    /** 🔹 PK (Product의 GTIN과 동일하게 사용) */
    @Id
    @Column(name = "GTIN")
    private String gtin; // Product의 PK와 동일한 값

    /** 🔹 Product와 1:1 관계 — GTIN이 FK이자 PK */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // ✅ Product의 PK를 Price의 PK로 공유
    @JoinColumn(name = "GTIN")
    private Product product;

    /** 🔹 기준단가 */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRICE", nullable = false, insertable = false, updatable = false)
    @Comment("기준 단가 (점포에서 구입하는 가격)")
    private Product price;

    /** 🔹 매입단가 */
    @Column(name = "PURCHASE_PRICE", nullable = false)
    @Comment("매입 단가 (본사가 매입하는 가격)")
    private Long purchasePrice;

    /** 🔹 매출단가 */
    @Column(name = "SALE_PRICE", nullable = false)
    @Comment("매출 단가 (소비자에게 판매하는 가격)")
    private Long salePrice;

}
