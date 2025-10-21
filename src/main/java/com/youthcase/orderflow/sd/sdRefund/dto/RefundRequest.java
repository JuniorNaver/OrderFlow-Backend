package com.youthcase.orderflow.sd.sdRefund.dto;

import com.youthcase.orderflow.sd.sdRefund.domain.RefundReason;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequest {

    @NotNull
    private Long paymentId;

    @NotNull
    private Integer cancelAmount;

    @NotNull
    private RefundReason refundReason;  // ✅ Enum 타입

    private String detailReason;        // ✅ 기타 사유

    private String cardNo;

    // ✅ 환불할 상품 목록 (유통기한 포함)
    private List<RefundItemDTO> items;
}
