package com.youthcase.orderflow.pr.dto;

import com.youthcase.orderflow.master.product.domain.StorageMethod;

public record PRRecommendItemDto(
        String productCode,
        String productName,
        Integer suggestedQty,
        Integer unitPrice,
        StorageMethod storageMethod,
        String category,
        String reason,
        String unit,
        String imageUrl
) {}
