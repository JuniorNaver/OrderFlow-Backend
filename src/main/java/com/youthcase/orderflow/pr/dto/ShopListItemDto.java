package com.youthcase.orderflow.pr.dto;

import com.youthcase.orderflow.master.product.domain.StorageMethod;
import com.youthcase.orderflow.master.product.domain.Unit;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ShopListItemDto(
        Long id,
        String gtin,
        String productName,
        Unit unit,
        BigDecimal purchasePrice,      // 스냅샷
        StorageMethod storageMethod,
        String categoryCode,
        String categoryName,
        String imageUrl,
        String description,            // ShopList 메모
        Instant createdAt,
        Instant updatedAt,
        LocalDate orderDate,           // 선택: 요청 파라미터 echo
        LocalDate expectedDueDate      // 선택: orderDate + leadTime

){}
