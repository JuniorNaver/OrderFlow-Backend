package com.youthcase.orderflow.master.product.dto;

import com.youthcase.orderflow.master.product.domain.StorageMethod;
import com.youthcase.orderflow.master.product.domain.Unit;
import com.youthcase.orderflow.master.product.domain.ExpiryType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductCreateDTO(
        @Pattern(regexp="\\d{12,14}", message="GTIN은 12~14자리 숫자")// 생성 시엔 필수
        @NotBlank String gtin,
        @NotBlank @Size(max = 100) String productName,
        @NotNull Unit unit,
        @NotNull @Digits(integer=10, fraction=2) BigDecimal price,
        @NotNull StorageMethod storageMethod,
        @NotBlank String categoryCode,
        // 새 필드들
        @NotNull ExpiryType expiryType,           // 생성 시 명시(기본 NONE로 컨트롤러에서 default 가능)
        @Min(0) Integer shelfLifeDays,             // 옵션: (15/17) 없을 때 보험

        @Size(max = 1000)String imageUrl,

        @Size(max = 2000)
        String description,

        // 옵션: 최초 등록 시 발주가능 여부
        Boolean orderable,

        // 치수 (mm)
        @Min(0) @Max(999_999) Integer widthMm,
        @Min(0) @Max(999_999) Integer depthMm,
        @Min(0) @Max(999_999) Integer heightMm
) {}