package com.youthcase.orderflow.pr.dto;

public record KanDto(
        String kanCode,
        String label,
        long productCount,
        long childCount
) {}