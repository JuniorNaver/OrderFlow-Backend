package com.youthcase.orderflow.sd.sdReceipt.dto;

import com.youthcase.orderflow.sd.sdReceipt.domain.Receipt;
import com.youthcase.orderflow.sd.sdSales.dto.SalesItemDTO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ReceiptResponse {

    private String receiptNo;
    private LocalDateTime issuedAt;

    private String branchName;
    private String branchAddress;

    private BigDecimal totalAmount;
    private String paymentMethod;
    private String refundStatus;

    private List<SalesItemDTO> items;

    public static ReceiptResponse fromEntity(Receipt r) {
        return ReceiptResponse.builder()
                .receiptNo(r.getReceiptNo())
                .issuedAt(r.getIssuedAt())
                .storeName(r.getStore().getName())
                .storeAddress(r.getStore().getAddress())
                .totalAmount(r.getPaymentHeader().getTotalAmount())
                .paymentMethod(r.getPaymentHeader().getPaymentItems().get(0).getPaymentMethod().name())
                .refundStatus(r.getRefundHeader() != null ? "REFUNDED" : "NORMAL")
                .items(r.getSalesHeader().getSalesItems().stream()
                        .map(SalesItemDTO::fromEntity)
                        .toList())
                .build();
    }
}