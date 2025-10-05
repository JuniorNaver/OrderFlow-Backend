package com.youthcase.orderflow.sd.sdReceipt.dto;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptItemDTO {
    private String productName;       // 상품명
    private int quantity;             // 수량
    private BigDecimal unitPrice;     // 단가
    private BigDecimal totalPrice;    // 합계
    private PaymentMethod paymentMethod; // 결제수단 (카드/현금/간편결제)
}