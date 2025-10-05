package com.youthcase.orderflow.sd.sdRefund.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundItemDTO {
    private Long paymentItemId;
    private int refundQuantity;
    private int refundAmount;
}
