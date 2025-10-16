package com.youthcase.orderflow.sd.sdRefund.dto;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record VerifyRefundResponse(
        String impUid,
        String merchantUid,
        String status,          // paid / cancelled / failed
        Double cancelAmount,    // 실제 취소 금액
        String pgProvider,      // tosspayments / kakaopay 등
        String cancelReason,
        String receiptUrl,
        ZonedDateTime paidAt,
        ZonedDateTime cancelledAt
) {}