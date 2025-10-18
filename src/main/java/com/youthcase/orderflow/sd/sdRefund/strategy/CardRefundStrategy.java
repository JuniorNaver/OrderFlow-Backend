package com.youthcase.orderflow.sd.sdRefund.strategy;

import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundStatus;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("CARD")
@Slf4j
public class CardRefundStrategy implements RefundStrategy {

    @Override
    public boolean verify(RefundHeader header) {
        // 모의 카드결제: 내부 거래번호 유효성만 별도 검증하려면 여기서 처리 가능
        // (현재는 Controller의 /verify에서 처리하므로 true 가정)
        return true;
    }

    @Override
    public RefundResponse refund(RefundHeader header) {
        log.info("💳 카드 환불 처리 시작: {}", header.getRefundId());
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
