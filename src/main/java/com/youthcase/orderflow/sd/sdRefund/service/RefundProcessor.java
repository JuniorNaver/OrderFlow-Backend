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
        if (header.getPaymentHeader() == null ||
                header.getPaymentHeader().getPaymentItems() == null ||
                header.getPaymentHeader().getPaymentItems().isEmpty()) {
            throw new IllegalStateException("환불할 결제 항목이 존재하지 않습니다.");
        }

        String method = header.getPaymentHeader()
                .getPaymentItems()
                .get(0)
                .getPaymentMethod()
                .name(); // "CARD" | "CASH" | "EASY"

        RefundStrategy strategy = strategyMap.get(method);
            if (strategy == null) {
                throw new IllegalArgumentException("지원하지 않는 환불 방식: " + method);
            }

        log.info("▶️ [{}] 환불 처리 시작 (refundId={})", method, header.getRefundId());

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

        return strategy.refund(header);
    }
}
