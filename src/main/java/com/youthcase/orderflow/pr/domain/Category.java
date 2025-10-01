package com.youthcase.orderflow.pr.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CATEGORY")
@Getter
@Setter
public class Category {

    @Id
    @Column(name = "KAN_CODE", length = 20)
    private String kanCode; // 카테고리 코드 (PK)

    @Column(name = "TOTAL_CATEGORY", length = 50)
    private String totalCategory; // 총분류

    @Column(name = "LARGE_CATEGORY", length = 50)
    private String largeCategory; // 대분류

    @Column(name = "MEDIUM_CATEGORY", length = 50)
    private String mediumCategory; // 중분류

    @Column(name = "SMALL_CATEGORY", length = 60)
    private String smallCategory; // 소분류

    // PRODUCT 테이블과 FK 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GTIN")
    private Product product; // 상품 FK
}
