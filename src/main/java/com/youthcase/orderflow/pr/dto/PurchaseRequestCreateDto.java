package com.youthcase.orderflow.pr.dto;


import java.time.LocalDate;

public record PurchaseRequestCreateDto(
        String gtin,
        Long qty,
        LocalDate expectedDate // 선택값이면 null 허용
) {}
