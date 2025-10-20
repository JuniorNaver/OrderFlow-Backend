package com.youthcase.orderflow.sd.sdReceipt.dto;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentMethod;
import com.youthcase.orderflow.sd.sdReceipt.domain.Receipt;
import com.youthcase.orderflow.sd.sdSales.dto.SalesItemDTO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptResponse {

    private String receiptNo;
    private LocalDateTime issuedAt;

    private String storeName;
    private String storeAddress;

    private BigDecimal totalAmount;
    private String refundStatus;
    private Long paymentId;

    // ✅ 판매 상품 내역
    private List<SalesItemDTO> items;

    // ✅ 결제내역 (분할결제 전체 포함)
    private List<PaymentItemDTO> payments;

    public static ReceiptResponse fromEntity(Receipt r) {
        if (r == null) return null;

        return ReceiptResponse.builder()
                .receiptNo(r.getReceiptNo())
                .issuedAt(r.getIssuedAt())
                .storeName(r.getStore() != null ? r.getStore().getStoreName() : "매장정보 없음")
                .storeAddress(r.getStore() != null ? r.getStore().getAddress() : "")
                .totalAmount(r.getPaymentHeader() != null ? r.getPaymentHeader().getTotalAmount() : BigDecimal.ZERO)
                .refundStatus(r.getRefundHeader() != null ? "REFUNDED" : "NORMAL")
                .paymentId(r.getPaymentHeader() != null ? r.getPaymentHeader().getPaymentId() : null)
                .items(r.getSalesHeader() != null
                        ? r.getSalesHeader().getSalesItems().stream()
                        .map(SalesItemDTO::from)
                        .toList()
                        : List.of())
                .payments(r.getPaymentHeader() != null
                        ? r.getPaymentHeader().getPaymentItems().stream()
                        .map(PaymentItemDTO::from)
                        .collect(Collectors.toList())
                        : List.of())
                .build();
    }

    // ✅ 내부 DTO (결제 내역)
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentItemDTO {
        private String paymentMethod;   // CARD, CASH, EASY
        private String provider;        // KAKAOPAY, TOSSPAY
        private String approvalNo;      // 승인번호
        private String cardNo;          // 카드번호 (마스킹)
        private BigDecimal paidAmount;  // 결제 금액

        public static PaymentItemDTO from(PaymentItem p) {
            return PaymentItemDTO.builder()
                    .paymentMethod(p.getPaymentMethod() != null ? p.getPaymentMethod().name() : "UNKNOWN")
                    .provider(p.getPaymentMethod() == PaymentMethod.EASY ? "KAKAOPAY"
                    : p.getPaymentMethod() == PaymentMethod.CARD ? "VISA"
                    : "CASH"
                    ) // 혹은 결제수단별로 하드코딩 가능
                    .approvalNo(p.getTransactionNo()) // ✅ 승인번호
                    .cardNo(maskCardNumber(p.getImpUid())) // ✅ impUid는 거래고유ID (마스킹 처리)
                    .paidAmount(p.getAmount()) // ✅ 결제금액
                    .build();
        }


        // ✅ 카드번호 마스킹 처리
        private static String maskCardNumber(String id) {
            if (id == null || id.length() < 4) return null;
            return "****-****-****-"+ id.substring(id.length() - 4);
        }
    }
}