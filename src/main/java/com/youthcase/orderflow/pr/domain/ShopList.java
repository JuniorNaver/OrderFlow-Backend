package com.youthcase.orderflow.pr.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "SHOP_LIST")
@Getter
@Setter
public class ShopList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PR_ITEM_ID")
    private Long prItemId;   // 발주 요청 리스트 ID (PK)

    @Column(name = "PRODUCT_IMAGE", nullable = false, length = 255)
    private String productImage;   // 상품 이미지

    @Enumerated(EnumType.STRING)
    @Column(name = "AVAILABLE", nullable = false, length = 20)
    private AvailableStatus available;   // 발주 가능 여부 (ENUM)

    @Column(name = "LEAD_TIME_DAYS", length = 100)
    private String leadTimeDays;   // 발주 후 예상일수

    @Column(name = "PRODUCT_DESCRIPTION", length = 2000)
    private String productDescription;   // 제품 상세 설명

    // 상품 고유 코드 (FK: PRODUCT 테이블)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GTIN", nullable = false)
    private Product product;
}