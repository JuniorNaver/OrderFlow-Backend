package com.youthcase.orderflow.pr.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Entity
@Table(name = "PRODUCT")
@Getter @Setter
public class Product {
    @Id
    @Column(name = "GTIN", length = 14, nullable = false)
    private String gtin;

    @Column(name = "PRODUCT_NAME", nullable = false, length = 100)
    private String productName;

    @Enumerated(EnumType.STRING)
    @Column(name = "UNIT", nullable = false, length = 20)
    private Unit unit;

    @Column(name = "PRICE", precision = 12, scale = 2, nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "STORAGE_METHOD", nullable = false, length = 20)
    private StorageMethod storageMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KAN_CODE", nullable = false, referencedColumnName = "KAN_CODE")
    private Category category;

    @Column(name="IMAGE_URL", length=230)
    private String imageUrl;

    @Column(name="DESCRIPTION", length=2000)
    private String description;

    @Column(name = "ORDERABLE", nullable = false)
    private Boolean orderable = Boolean.TRUE; // 기본값: 발주 가능
}