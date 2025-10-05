package com.youthcase.orderflow.pr.dto;

import com.youthcase.orderflow.pr.domain.StorageMethod;
import com.youthcase.orderflow.pr.domain.Unit;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequestDto(
        @NotBlank
        @Pattern(regexp="\\d{12,14}", message="GTIN은 12~14자리 숫자")
        String gtin,
        @NotBlank String productName,
        @NotNull Unit unit,
        @NotNull @Digits(integer=10, fraction=2) BigDecimal price,
        @NotNull StorageMethod storageMethod,
        @NotBlank String categoryCode,
        Boolean orderable
) {}

