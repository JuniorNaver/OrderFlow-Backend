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

        // ✅ 1. 결제 항목 유효성 검사
        if (header.getPaymentHeader() == null ||
                header.getPaymentHeader().getPaymentItems() == null ||
                header.getPaymentHeader().getPaymentItems().isEmpty()) {
            throw new IllegalStateException("환불할 결제 항목이 존재하지 않습니다.");
        }

        // ✅ 2. 첫 번째 결제 항목에서 결제 수단 추출
        String method = header.getPaymentHeader()
                .getPaymentItems()
                .stream()
                .findFirst() // ✅ Set은 get(index) 안 되므로 findFirst() 사용
                .orElseThrow(() -> new IllegalStateException("결제 항목이 비어 있습니다."))
                .getPaymentMethod()
                .name(); // "CARD" | "CASH" | "EASY"

        // ✅ 3. 전략 선택
        RefundStrategy strategy = strategyMap.get(method);
        if (strategy == null) {
            throw new IllegalArgumentException("지원하지 않는 환불 방식: " + method);
        }

        log.info("▶️ [{}] 환불 처리 시작 (refundId={})", method, header.getRefundId());

        // ✅ 4. 환불 검증 (PG 연동 또는 내부 검증)
        boolean verified = strategy.verify(header);
        if (!verified) {
            header.setRefundStatus(RefundStatus.FAILED);
            return RefundResponse.builder()
                    .refundId(header.getRefundId())
                    .paymentId(header.getPaymentHeader().getPaymentId())
                    .refundAmount(header.getRefundAmount().doubleValue())
                    .refundStatus(RefundStatus.FAILED)
                    .reason("PG/내부 검증 실패")
                    .requestedTime(header.getRequestedTime())
                    .approvedTime(header.getApprovedTime())
                    .build();
        }

        // ✅ 5. 실제 환불 수행 (전략별 refund 메서드 실행)
        return strategy.refund(header);
    }
}
