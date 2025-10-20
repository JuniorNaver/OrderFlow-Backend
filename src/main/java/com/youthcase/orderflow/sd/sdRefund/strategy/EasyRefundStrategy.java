package com.youthcase.orderflow.sd.sdRefund.strategy;

import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundStatus;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundResponse;
import com.youthcase.orderflow.sd.sdRefund.service.RefundIamportService;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("EASY")
@Slf4j
@RequiredArgsConstructor
public class EasyRefundStrategy implements RefundStrategy {

    private final RefundIamportService iamport;

    @Override
    public boolean verify(RefundHeader header) {
        try {
            // ✅ Set → Stream으로 첫 번째 결제 항목 가져오기
            PaymentItem firstItem = header.getPaymentHeader()
                    .getPaymentItems()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("간편결제 환불 검증 실패: 결제 항목이 존재하지 않습니다."));

            String impUid = firstItem.getImpUid();
            if (impUid == null || impUid.isBlank()) {
                log.error("❌ PG 검증 실패: impUid가 비어 있습니다.");
                return false;
            }

            var res = iamport.verifyPayment(impUid);
            log.info("✅ PG 검증 성공: imp_uid={}, status={}", res.impUid(), res.status());
            return "paid".equals(res.status()) || "ready".equals(res.status());

        } catch (Exception e) {
            log.error("❌ PG 검증 중 오류 발생", e);
            return false;
        }
    }

    @Override
    public RefundResponse refund(RefundHeader header) {
        RefundStatus resultStatus;

        try {
            // ✅ 첫 번째 PaymentItem 가져오기
            PaymentItem firstItem = header.getPaymentHeader()
                    .getPaymentItems()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("간편결제 환불 실패: 결제 항목이 존재하지 않습니다."));

            String impUid = firstItem.getImpUid();
            if (impUid == null || impUid.isBlank()) {
                throw new IllegalStateException("간편결제 환불 실패: impUid가 없습니다.");
            }

            boolean ok = iamport.cancelPayment(impUid, header.getReason(), header.getRefundAmount().doubleValue());

            if (ok) {
                header.setRefundStatus(RefundStatus.COMPLETED);
                header.setApprovedTime(LocalDateTime.now());
                log.info("✅ 간편결제 환불 성공: imp_uid={}", impUid);
            } else {
                header.setRefundStatus(RefundStatus.FAILED);
                log.warn("⚠️ 간편결제 환불 실패 (PG 응답 거부): imp_uid={}", impUid);
            }

        } catch (Exception e) {
            header.setRefundStatus(RefundStatus.FAILED);
            log.error("❌ 간편결제 환불 처리 중 예외 발생", e);
        }

        resultStatus = header.getRefundStatus();

        return RefundResponse.builder()
                .refundId(header.getRefundId())
                .paymentId(header.getPaymentHeader().getPaymentId())
                .refundAmount(header.getRefundAmount().doubleValue())
                .refundStatus(resultStatus)
                .reason(header.getReason())
                .requestedTime(header.getRequestedTime())
                .approvedTime(header.getApprovedTime())
                .build();
    }
}
