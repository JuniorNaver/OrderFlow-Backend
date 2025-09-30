package com.youthcase.orderflow.sd.sdSales.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity

public class ProductMaster {
    @Id
    @Column(length = 14) // GTIN(바코드): 8, 12, 13, 14 자리 가능
    private String gtin;

    @Column(nullable = false, length = 200)
    private String name; // 상품명

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice; // 단가

    @Column(length = 50)
    private String category; // 카테고리 (음료, 식품 등)

    @Column(length = 200)
    private String description; // 상품 설명 (선택)

    // 필요하다면 추가:
    // private String manufacturer;
    // private LocalDate expirationDate; (유통기한 관리할 경우)
}
