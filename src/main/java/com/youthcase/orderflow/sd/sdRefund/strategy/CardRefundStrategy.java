package com.youthcase.orderflow.sd.sdRefund.strategy;

import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundStatus;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundResponse;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("CARD")
@Slf4j
public class CardRefundStrategy implements RefundStrategy {

    @Override
    public boolean verify(RefundHeader header) {
        try {
            PaymentItem item = header.getPaymentHeader()
                    .getPaymentItems()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("결제 항목이 존재하지 않습니다."));

            String storedTxNo = item.getTransactionNo();      // 결제 승인번호
            String enteredCardNo = header.getDetailReason();  // 프론트 입력 카드번호(마지막 4자리)

            log.info("💳 카드 검증 시작: storedTxNo={}, entered={}", storedTxNo, enteredCardNo);

            boolean match = storedTxNo != null && enteredCardNo != null && storedTxNo.endsWith(enteredCardNo);
            if (!match) {
                log.warn("❌ 카드번호 불일치 - storedTxNo={}, entered={}", storedTxNo, enteredCardNo);
                return false;
            }

            log.info("✅ 카드번호 검증 통과");
            return true;

        } catch (Exception e) {
            log.error("❌ 카드 검증 중 오류", e);
            return false;
        }
    }

    @Override
    public RefundResponse refund(RefundHeader header) {
        log.info("💳 카드 환불 처리 시작: refundId={}", header.getRefundId());

        header.setRefundStatus(RefundStatus.COMPLETED);
        header.setApprovedTime(LocalDateTime.now());

        return RefundResponse.builder()
                .refundId(header.getRefundId())
                .paymentId(header.getPaymentHeader().getPaymentId())
                .refundAmount(header.getRefundAmount().doubleValue())
                .refundStatus(RefundStatus.COMPLETED)
                .reason(header.getDetailReason())
                .requestedTime(header.getRequestedTime())
                .approvedTime(header.getApprovedTime())
                .build();
    }
}
