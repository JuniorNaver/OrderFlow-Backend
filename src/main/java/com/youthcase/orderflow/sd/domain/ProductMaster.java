package com.youthcase.orderflow.sd.domain;

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
    private String gtin;  // 바코드 고유코드

    private String name;
    private BigDecimal basePrice;
    private Number price;
}
