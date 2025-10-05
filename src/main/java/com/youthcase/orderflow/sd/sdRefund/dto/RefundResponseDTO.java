package com.youthcase.orderflow.sd.sdRefund.dto;

import com.youthcase.orderflow.sd.sdRefund.domain.RefundStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundResponseDTO {
    private Long refundId;
    private Long paymentId;
    private double refundAmount;
    private RefundStatus refundStatus;
    private String reason;
    private LocalDateTime requestedTime;
    private LocalDateTime approvedTime;
    private List<RefundItemDTO> items;
}
