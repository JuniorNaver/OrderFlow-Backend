package com.youthcase.orderflow.sd.sdReceipt.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptResponseDTO {
    private Long receiptId;             // 영수증 번호
    private LocalDateTime receiptDate;  // 발행 시각
    private String storeName;           // 매장명
    private BigDecimal totalAmount;     // 총 금액
    private Long paymentId;             // 결제 ID
    private Long salesId;               // 판매 ID
    private List<ReceiptItemDTO> items; // 상세 상품 목록
}
