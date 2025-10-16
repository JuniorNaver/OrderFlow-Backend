package com.youthcase.orderflow.pr.dto;

import com.youthcase.orderflow.pr.domain.enums.ExpiryType;
import com.youthcase.orderflow.pr.domain.enums.StorageMethod;
import com.youthcase.orderflow.pr.domain.enums.Unit;

import java.math.BigDecimal;

public record ProductResponseDto(
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

