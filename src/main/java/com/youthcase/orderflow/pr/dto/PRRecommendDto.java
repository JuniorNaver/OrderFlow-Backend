package com.youthcase.orderflow.pr.dto;


import java.util.List;

public record PRRecommendDto(
        String storeId,
        String generatedAt,
        List<PRRecommendItemDto> items
) {}

