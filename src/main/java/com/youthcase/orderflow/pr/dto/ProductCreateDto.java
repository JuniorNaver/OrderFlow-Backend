package com.youthcase.orderflow.pr.dto;

import com.youthcase.orderflow.pr.domain.enums.StorageMethod;
import com.youthcase.orderflow.pr.domain.enums.Unit;
import com.youthcase.orderflow.pr.domain.enums.ExpiryType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductCreateDto(
        @Pattern(regexp="\\d{12,14}", message="GTIN은 12~14자리 숫자")// 생성 시엔 필수
        @NotBlank String gtin,
        @NotBlank String productName,
        @NotNull Unit unit,
        @NotNull @Digits(integer=10, fraction=2) BigDecimal price,
        @NotNull StorageMethod storageMethod,
        @NotBlank String categoryCode,
        // 새 필드들
        @NotNull ExpiryType expiryType,           // 생성 시 명시(기본 NONE로 컨트롤러에서 default 가능)
        @Min(0) Integer shelfLifeDays             // 옵션: (15/17) 없을 때 보험
) {}