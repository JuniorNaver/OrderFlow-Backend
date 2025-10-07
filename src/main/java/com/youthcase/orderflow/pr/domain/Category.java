package com.youthcase.orderflow.pr.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "CATEGORY")
@Getter
@Setter
public class Category {

    @Id
    @Column(name = "KAN_CODE", length = 20)
    private String kanCode; // 카테고리 코드 (PK)

    @Column(name = "TOTAL_CATEGORY", length = 100)
    private String totalCategory; // 총분류

    @Column(name = "LARGE_CATEGORY", length = 100)
    private String largeCategory; // 대분류

    @Column(name = "MEDIUM_CATEGORY", length = 100)
    private String mediumCategory; // 중분류

    @Column(name = "SMALL_CATEGORY", length = 120)
    private String smallCategory; // 소분류

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Product> products; // 카테고리에 속한 상품들
}
