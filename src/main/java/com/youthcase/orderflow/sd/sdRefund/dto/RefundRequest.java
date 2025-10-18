package com.youthcase.orderflow.sd.sdRefund.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequest {
    @NotNull
    private Long paymentId;     // ✅ 환불은 반드시 Payment 기준으로
    @NotNull
    private Integer cancelAmount;
    private String reason;
}
