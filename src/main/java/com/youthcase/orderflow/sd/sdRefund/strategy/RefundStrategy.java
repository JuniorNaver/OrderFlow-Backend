package com.youthcase.orderflow.sd.sdRefund.strategy;

import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundResponse;

public interface RefundStrategy {
    RefundResponse refund(RefundHeader refundHeader); // 환불 실행
    boolean verify(RefundHeader refundHeader);           // PG 검증 (간편결제 전용)
}
