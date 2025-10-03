package com.youthcase.orderflow.pr.DTO;

import com.youthcase.orderflow.pr.domain.StorageMethod;
import com.youthcase.orderflow.pr.domain.Unit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ShopListRequestDto(
        @NotNull String productId,
        @NotBlank String productImage,
        @NotBlank String productDescription,
        @NotNull LocalDate orderDate  // 발주일 필드 추가
) {}