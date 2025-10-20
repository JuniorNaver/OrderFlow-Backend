package com.youthcase.orderflow.master.product.dto;

import com.youthcase.orderflow.master.product.domain.ExpiryType;
import com.youthcase.orderflow.master.product.domain.StorageMethod;
import com.youthcase.orderflow.master.product.domain.Unit;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductUpdateDTO(
        @NotBlank String productName,
        @NotNull Unit unit,
        @NotNull @Digits(integer=10, fraction=2) BigDecimal price,
        @NotNull StorageMethod storageMethod,
        @NotBlank String categoryCode,
        @NotNull ExpiryType expiryType,
        @Min(0) Integer shelfLifeDays,
        @Size(max = 1000)
        String imageUrl,

        @Size(max = 2000)
        String description,

        @NotNull
        Boolean orderable,

        @Min(0) @Max(999_999) Integer widthMm,
        @Min(0) @Max(999_999) Integer depthMm,
        @Min(0) @Max(999_999) Integer heightMm
) {}
