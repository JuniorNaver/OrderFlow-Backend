package com.youthcase.orderflow.sd.sdRefund.strategy;

import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundStatus;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundResponse;
import com.youthcase.orderflow.sd.sdRefund.service.RefundIamportService;
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
            String impUid = header.getPaymentHeader().getPaymentItems().get(0).getImpUid();
            var res = iamport.verifyPayment(impUid);
            log.info("✅ PG 검증 성공: imp_uid={}, status={}", res.impUid(), res.status());
            return "paid".equals(res.status()) || "ready".equals(res.status());
        } catch (Exception e) {
            log.error("❌ PG 검증 중 오류", e);
            return false;
        }
    }

    @Override
    public RefundResponse refund(RefundHeader header) {
        try {
            String impUid = header.getPaymentHeader().getPaymentItems().get(0).getImpUid();
            boolean ok = iamport.cancelPayment(impUid, header.getReason(), header.getRefundAmount().doubleValue());

            if (ok) {
                header.setRefundStatus(RefundStatus.COMPLETED);
                header.setApprovedTime(LocalDateTime.now());
                log.info("✅ 간편결제 환불 성공: imp_uid={}", impUid);
            } else {
                header.setRefundStatus(RefundStatus.FAILED);
                log.warn("⚠️ 간편결제 환불 실패: imp_uid={}", impUid);
            }
        } catch (Exception e) {
            header.setRefundStatus(RefundStatus.FAILED);
            log.error("❌ 간편결제 환불 실패", e);
        }

        return RefundResponse.builder()
                .refundId(header.getRefundId())
                .paymentId(header.getPaymentHeader().getPaymentId())
                .refundAmount(header.getRefundAmount().doubleValue())
                .refundStatus(header.getRefundStatus())
                .reason(header.getReason())
                .requestedTime(header.getRequestedTime())
                .approvedTime(header.getApprovedTime())
                .build();
    }
}
