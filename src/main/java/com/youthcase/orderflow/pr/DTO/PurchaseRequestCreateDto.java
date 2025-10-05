package com.youthcase.orderflow.pr.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public record PurchaseRequestCreateDto(
        String gtin,
        int qty,
        LocalDate expectedDate // 선택값이면 null 허용
) {}
