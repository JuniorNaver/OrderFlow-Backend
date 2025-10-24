package com.youthcase.orderflow.pr.dto;

import java.time.LocalDate;

public record PurchaseRequestDto(
        Long id,
        String storeId,
        String gtin,
        Long qty,
        LocalDate expectedDate,
        String status
) {}