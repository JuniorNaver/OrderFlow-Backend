package com.youthcase.orderflow.pr.dto;

import com.youthcase.orderflow.pr.domain.StorageMethod;
import com.youthcase.orderflow.pr.domain.Unit;

import java.math.BigDecimal;

public record ProductResponseDto(
        String gtin,
        String productName,
        Unit unit,
        BigDecimal price,
        StorageMethod storageMethod,
        String categoryCode,
        String categoryName
) {}

// dto폴더 변경이안돼