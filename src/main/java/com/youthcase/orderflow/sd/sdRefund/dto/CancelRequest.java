package com.youthcase.orderflow.sd.sdRefund.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CancelRequest(
        @NotBlank String impUid,
        @NotNull BigDecimal cancelAmount,   // 부분취소 시 금액, 전체취소면 결제금액과 동일
        String reason
) {}