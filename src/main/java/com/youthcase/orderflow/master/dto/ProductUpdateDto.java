package com.youthcase.orderflow.master.dto;

import com.youthcase.orderflow.master.domain.ExpiryType;
import com.youthcase.orderflow.master.domain.StorageMethod;
import com.youthcase.orderflow.master.domain.Unit;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductUpdateDto(
        @NotBlank String productName,
        @NotNull Unit unit,
        @NotNull @Digits(integer=10, fraction=2) BigDecimal price,
        @NotNull StorageMethod storageMethod,
        @NotBlank String categoryCode,
        @NotNull ExpiryType expiryType,
        @Min(0) Integer shelfLifeDays
) {}
