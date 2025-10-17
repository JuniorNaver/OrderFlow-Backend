package com.youthcase.orderflow.master.product.dto;

import com.youthcase.orderflow.master.product.domain.ExpiryType;
import com.youthcase.orderflow.master.product.domain.StorageMethod;
import com.youthcase.orderflow.master.product.domain.Unit;

import java.math.BigDecimal;

public record ProductResponseDTO(
        String gtin,
        String productName,
        Unit unit,
        BigDecimal price,
        StorageMethod storageMethod,
        String categoryCode,
        String categoryName,
        ExpiryType expiryType,   // ← 추가
        Integer shelfLifeDays,   // ← 추가(없으면 null)
        Boolean orderable        // ← 선택: 프론트에 보여줄 거면 포함

) {}

