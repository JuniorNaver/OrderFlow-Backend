package com.youthcase.orderflow.pr.DTO;

import com.youthcase.orderflow.pr.domain.StorageMethod;
import com.youthcase.orderflow.pr.domain.Unit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductUpdateDto(
        @NotBlank
        String productName,
        @NotNull
        Unit unit,
        @NotNull
        @Positive
        Double price,
        @NotNull
        StorageMethod storageMethod
) {}
