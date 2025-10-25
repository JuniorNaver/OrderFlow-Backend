package com.youthcase.orderflow.pr.dto;

import com.youthcase.orderflow.master.product.domain.StorageMethod;

import java.math.BigDecimal;

public record PRRecommendItemDto(
        String productCode,
        String productName,
        Long suggestedQty,
        BigDecimal unitPrice,
        StorageMethod storageMethod,
        String category,
        String reason,
        String unit,
        String imageUrl
) {}
