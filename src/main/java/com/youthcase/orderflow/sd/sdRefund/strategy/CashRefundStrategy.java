package com.youthcase.orderflow.sd.sdRefund.strategy;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundStatus;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
@Service("CASH")
@Slf4j
public class CashRefundStrategy implements RefundStrategy {

    @Override
    public boolean verify(RefundHeader header) {
        // 현금 환불은 별도 PG 검증 없음 (영수증 검증은 Controller 단계 완료 가정)
        return true;
    }

    @Override
    public RefundResponse refund(RefundHeader header) {
        log.info("💵 현금 환불 처리: {}", header.getRefundId());
        header.setRefundStatus(RefundStatus.COMPLETED);
        header.setApprovedTime(LocalDateTime.now());
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
