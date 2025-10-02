package com.youthcase.orderflow.pr.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PRODUCT")
@Getter
@Setter
public class Product {

    @Id
    @Column(name = "GTIN")
    private String gtin;   // 상품 고유 코드 (PK)

    @Column(name = "PRODUCT_NAME", nullable = false, length = 100)
    private String productName;   // 상품명

    @Enumerated(EnumType.STRING)
    @Column(name = "UNIT", nullable = false, length = 20)
    private Unit unit;          // 상품 단위

    @Column(name = "PRICE", nullable = false, precision = 10, scale = 2)
    private Double price;         // 상품 단가

    @Enumerated(EnumType.STRING)
    @Column(name = "STORAGE_METHOD", nullable = false, length = 20)
    private StorageMethod storageMethod; // 보관방법

}