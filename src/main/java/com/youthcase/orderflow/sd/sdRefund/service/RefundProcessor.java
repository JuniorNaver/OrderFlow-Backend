package com.youthcase.orderflow.sd.sdRefund.service;

import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundStatus;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundResponse;
import com.youthcase.orderflow.sd.sdRefund.strategy.RefundStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundProcessor {

    private final Map<String, RefundStrategy> strategyMap;

    @Transactional
    public RefundResponse processRefund(RefundHeader header) {
        String method = header.getPaymentHeader()
                .getPaymentItems()
                .get(0)
                .getPaymentMethod()
                .name(); // CARD / CASH / EASY

        RefundStrategy strategy = strategyMap.get(method);
        if (strategy == null)
            throw new IllegalArgumentException("지원하지 않는 환불 방식: " + method);

        log.info("▶️ [{}] 환불 처리 시작 (refundId={})", method, header.getRefundId());

        boolean verified = strategy.verify(header);
        if (!verified) {
            header.setRefundStatus(RefundStatus.FAILED);
            return RefundResponse.builder()
                    .refundId(header.getRefundId())
                    .refundStatus(RefundStatus.FAILED)
                    .reason("PG 검증 실패")
                    .build();
        }

        return strategy.refund(header);
    }
}