package com.youthcase.orderflow.pr.dto;

import com.youthcase.orderflow.master.product.domain.StorageMethod;
import com.youthcase.orderflow.master.product.domain.Unit;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ShopListItemDto(
        String gtin,
        String productName,
        Unit unit,
        BigDecimal price,
        StorageMethod storageMethod,
        String categoryCode,
        String categoryName,
        String imageUrl,
        String description,
        LocalDate orderDate,       // 요청 파라미터로 받은 값 그대로 에코
        LocalDate expectedDueDate  // orderDate + leadTime
){}
