package com.youthcase.orderflow.sd.sdRefund.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyRefundRequest(
        @NotBlank String impUid,     // 아임포트 거래 고유 ID
        @NotBlank String merchantUid // 가맹점 주문번호
) {}