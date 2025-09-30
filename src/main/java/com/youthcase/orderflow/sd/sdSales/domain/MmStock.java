package com.youthcase.orderflow.sd.sdSales.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class MmStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gtin", nullable = false)
    private ProductMaster product;

    @Column(nullable = false)
    private int quantity;
}