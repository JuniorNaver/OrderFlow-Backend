package com.youthcase.orderflow.pr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ShopListCommandDto(
        @NotBlank String gtin,               // PATCH에선 무시
        @Size(max = 2000) String description,
        Boolean orderable                    // null이면 기본 true
) { }
