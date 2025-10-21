package com.youthcase.orderflow.sd.sdRefund.service;

import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundStatus;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundResponse;
import com.youthcase.orderflow.sd.sdRefund.strategy.RefundStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundProcessor {

    private final Map<String, RefundStrategy> strategyMap;
    private final RefundInventoryService refundInventoryService;

    @Transactional
    public RefundResponse processRefund(RefundHeader header) {

        // 1️⃣ 기본 유효성 검사
        if (header.getPaymentHeader() == null ||
                header.getPaymentHeader().getPaymentItems() == null ||
                header.getPaymentHeader().getPaymentItems().isEmpty()) {
            throw new IllegalStateException("환불할 결제 항목이 존재하지 않습니다.");
        }

        // 2️⃣ 결제수단 추출
        String method = header.getPaymentHeader()
                .getPaymentItems()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("결제 항목이 비어 있습니다."))
                .getPaymentMethod()
                .name();

        RefundStrategy strategy = strategyMap.get(method);
        if (strategy == null) {
            throw new IllegalArgumentException("지원하지 않는 환불 방식: " + method);
        }

        log.info("▶️ [{}] 환불 처리 시작 (refundId={})", method, header.getRefundId());
        header.setRefundStatus(RefundStatus.REQUESTED);

        // 3️⃣ 검증 단계
        boolean verified = strategy.verify(header);
        if (!verified) {
            header.setRefundStatus(RefundStatus.REJECTED);
            return RefundResponse.builder()
                    .refundId(header.getRefundId())
                    .paymentId(header.getPaymentHeader().getPaymentId())
                    .refundAmount(header.getRefundAmount().doubleValue())
                    .refundStatus(RefundStatus.REJECTED)
                    .reason("검증 실패: 카드번호 또는 결제정보 불일치")
                    .requestedTime(header.getRequestedTime())
                    .build();
        }

        // 4️⃣ 검증 성공 시 승인 상태로 전환
        header.setRefundStatus(RefundStatus.APPROVED);
        log.info("✅ 환불 검증 통과 → 상태: APPROVED");

        // 5️⃣ 실제 환불 처리
        RefundResponse result = strategy.refund(header);

        // 6️⃣ 전략에서 상태가 지정되지 않았다면 기본 COMPLETED 처리
        if (result.getRefundStatus() == null) {
            header.setRefundStatus(RefundStatus.COMPLETED);
            result.setRefundStatus(RefundStatus.COMPLETED);
        }

        // ✅ 7️⃣ 환불 완료 후 재고 복원
        if (header.getRefundStatus() == RefundStatus.COMPLETED) {
            header.getPaymentHeader()
                    .getSalesHeader()
                    .getSalesItems()
                    .forEach(item -> {
                        LocalDate expDate = (item.getStk() != null && item.getStk().getLot() != null)
                                ? item.getStk().getLot().getExpDate()
                                : LocalDate.now().plusMonths(6); // 기본 6개월 유통기한
                        refundInventoryService.restoreStock(item, expDate);
                    });
        }

        log.info("✅ 환불 완료: refundId={}, status={}", header.getRefundId(), header.getRefundStatus());
        return result;
    }
}
