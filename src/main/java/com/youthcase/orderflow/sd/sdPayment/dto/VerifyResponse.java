package com.youthcase.orderflow.sd.sdPayment.dto;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record VerifyResponse(
        String impUid,
        String merchantUid,
        String transactionNo,     // 내부 승인번호 (CARD, CASH)
        String status,          // "paid", "ready", "failed" 등
        Integer amount,         // 결제 금액
        String currency,        // KRW
        String pgProvider,      // kakaopay / tosspayments ...
        String payMethod,       // card, vbank ...
        String buyerName,
        String buyerEmail,
        String buyerTel,
        String receiptUrl,
        ZonedDateTime paidAt
) {}