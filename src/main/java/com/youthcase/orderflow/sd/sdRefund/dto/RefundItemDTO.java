package com.youthcase.orderflow.sd.sdRefund.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundItemDTO {

    private String gtin;         // ✅ 상품 바코드
    private Long quantity;        // ✅ 환불 수량
    private LocalDate expDate;   // ✅ 유통기한 (프론트에서 입력받음)
}
