package com.youthcase.orderflow.sd.sdReceipt.service;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.repository.PaymentHeaderRepository;
import com.youthcase.orderflow.sd.sdReceipt.domain.ReceiptHeader;
import com.youthcase.orderflow.sd.sdReceipt.domain.ReceiptItem;
import com.youthcase.orderflow.sd.sdReceipt.dto.ReceiptItemDTO;
import com.youthcase.orderflow.sd.sdReceipt.dto.ReceiptResponseDTO;
import com.youthcase.orderflow.sd.sdReceipt.repository.ReceiptHeaderRepository;
import com.youthcase.orderflow.sd.sdReceipt.repository.ReceiptItemRepository;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import com.youthcase.orderflow.sd.sdSales.repository.SalesItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptHeaderRepository receiptHeaderRepository;
    private final ReceiptItemRepository receiptItemRepository;
    private final PaymentHeaderRepository paymentHeaderRepository;
    private final SalesItemRepository salesItemRepository;

    @Override
    public ReceiptResponseDTO createReceipt(Long paymentId) {
        // 1) 결제 헤더 확인
        PaymentHeader paymentHeader = paymentHeaderRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다. paymentId=" + paymentId));

        // 2) 기존 영수증 존재 시 예외
        receiptHeaderRepository.findByPaymentHeader_PaymentId(paymentId)
                .ifPresent(r -> {
                    throw new IllegalStateException("이미 영수증이 존재합니다. receiptId=" + r.getReceiptId());
                });

        // 3) 판매 아이템 조회
        Long orderId = paymentHeader.getSalesHeader() != null ? paymentHeader.getSalesHeader().getOrderId() : null;
        List<SalesItem> salesItems = orderId == null ? List.of()
                : salesItemRepository.findBySalesHeader_OrderId(orderId);

        // 4) 헤더 생성
        ReceiptHeader header = ReceiptHeader.builder()
                .paymentHeader(paymentHeader)
                .salesHeader(paymentHeader.getSalesHeader())
                .receiptNo("RCPT-" + UUID.randomUUID())
                .totalAmount(paymentHeader.getTotalAmount())
                .build();

        // 5) 아이템 생성
        List<ReceiptItem> items = salesItems.stream().map(si ->
                ReceiptItem.builder()
                        .receiptHeader(header)
                        .salesItem(si)
                        .quantity(si.getSalesQuantity())
                        .unitPrice(si.getSdPrice())
                        .totalPrice(si.getSdPrice()
                                .multiply(BigDecimal.valueOf(si.getSalesQuantity())))
                        .build()
        ).toList();

        header.setItems(items);

        // 6) 저장(cascade로 아이템 함께 저장)
        ReceiptHeader saved = receiptHeaderRepository.save(header);

        return toDto(saved);
    }

    @Override
    public ReceiptResponseDTO getReceiptByPaymentId(Long paymentId) {
        ReceiptHeader header = receiptHeaderRepository.findByPaymentHeader_PaymentId(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("영수증을 찾을 수 없습니다. paymentId=" + paymentId));
        return toDto(header);
    }

    @Override
    public ReceiptResponseDTO getReceipt(Long receiptId) {
        ReceiptHeader header = receiptHeaderRepository.findById(receiptId)
                .orElseThrow(() -> new IllegalArgumentException("영수증을 찾을 수 없습니다. receiptId=" + receiptId));
        return toDto(header);
    }

    // ===== Mapper =====
    private ReceiptResponseDTO toDto(ReceiptHeader h) {
        return ReceiptResponseDTO.builder()
                .receiptId(h.getReceiptId())
                .receiptNo(h.getReceiptNo())
                .paymentId(h.getPaymentHeader() != null ? h.getPaymentHeader().getPaymentId() : null)
                .salesId(h.getSalesHeader() != null ? h.getSalesHeader().getOrderId() : null)
                .refundId(h.getRefundHeader() != null ? h.getRefundHeader().getRefundId() : null)
                .totalAmount(h.getTotalAmount())
                .createdAt(h.getCreatedAt())
                .items(h.getItems() == null ? List.of() :
                        h.getItems().stream().map(this::toDto).toList())
                .build();
    }

    private ReceiptItemDTO toDto(ReceiptItem i) {
        return ReceiptItemDTO.builder()
                .receiptItemId(i.getReceiptItemId())
                .salesItemId(i.getSalesItem() != null ? i.getSalesItem().getNo() : null)
                .paymentItemId(i.getPaymentItem() != null ? i.getPaymentItem().getPaymentItemId() : null)
                .quantity(i.getQuantity())
                .unitPrice(i.getUnitPrice())
                .totalPrice(i.getTotalPrice())
                .build();
    }
}
