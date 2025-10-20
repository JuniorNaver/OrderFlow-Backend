package com.youthcase.orderflow.pr.service.browse.dto;

public record ProductItemDto(
        String gtin,
        String productName,
        String unit,          // 표시는 문자열로
        String price,         // "2,900"처럼 포맷해서 줄 거면 문자열이 편함
        String imageUrl,
        boolean orderable
) {}