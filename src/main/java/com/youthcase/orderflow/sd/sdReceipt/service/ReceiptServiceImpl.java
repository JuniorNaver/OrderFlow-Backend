package com.youthcase.orderflow.sd.sdReceipt.service;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdPayment.repository.PaymentHeaderRepository;
import com.youthcase.orderflow.sd.sdReceipt.domain.ReceiptHeader;
import com.youthcase.orderflow.sd.sdReceipt.domain.ReceiptItem;
import com.youthcase.orderflow.sd.sdReceipt.dto.ReceiptItemDTO;
import com.youthcase.orderflow.sd.sdReceipt.dto.ReceiptResponseDTO;
import com.youthcase.orderflow.sd.sdReceipt.repository.ReceiptHeaderRepository;
import com.youthcase.orderflow.sd.sdReceipt.repository.ReceiptItemRepository;
import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptHeaderRepository headerRepository;
    private final ReceiptItemRepository itemRepository;
    private final PaymentHeaderRepository paymentHeaderRepository;

    @Override
    @Transactional
    public ReceiptResponseDTO createReceipt(Long paymentId) {

        // 1️⃣ PaymentHeader 조회
        PaymentHeader paymentHeader = paymentHeaderRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다."));

        // 2️⃣ SalesHeader 연결
        SalesHeader salesHeader = paymentHeader.getSalesHeader();

        // 3️⃣ ReceiptHeader 생성
        ReceiptHeader receiptHeader = ReceiptHeader.builder()
                .paymentHeader(paymentHeader)
                .salesHeader(salesHeader)
                .receiptDate(LocalDateTime.now())
//                .storeName(salesHeader.getStoreName())
                .totalAmount(paymentHeader.getTotalAmount())
                .build();

        headerRepository.save(receiptHeader);

        // 4️⃣ ReceiptItem 생성
        List<ReceiptItem> receiptItems = new ArrayList<>();
        List<SalesItem> salesItems = salesHeader.getSalesItems();
        List<PaymentItem> paymentItems = paymentHeader.getPaymentItems();

        for (int i = 0; i < salesItems.size(); i++) {
            SalesItem sItem = salesItems.get(i);
            PaymentItem pItem = paymentItems.size() > i ? paymentItems.get(i) : null;

            receiptItems.add(ReceiptItem.builder()
                    .receiptHeader(receiptHeader)
                    .salesItem(sItem)
                    .paymentItem(pItem)
                    .build());
        }

        itemRepository.saveAll(receiptItems);
        receiptHeader.setItems(receiptItems);

        // 5️⃣ DTO 변환 후 반환
        return toResponseDTO(receiptHeader);
    }

    @Override
    public ReceiptResponseDTO getReceipt(Long receiptId) {
        ReceiptHeader header = headerRepository.findById(receiptId)
                .orElseThrow(() -> new RuntimeException("영수증을 찾을 수 없습니다."));
        return toResponseDTO(header);
    }

    @Override
    public ReceiptResponseDTO getByPaymentId(Long paymentId) {
        ReceiptHeader header = headerRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("결제에 대한 영수증이 존재하지 않습니다."));
        return toResponseDTO(header);
    }

    // 📘 내부 변환 메서드
    private ReceiptResponseDTO toResponseDTO(ReceiptHeader header) {

        return ReceiptResponseDTO.builder()
                .receiptId(header.getReceiptId())
                .receiptDate(header.getReceiptDate())
                .storeName(header.getStoreName())
                .totalAmount(header.getTotalAmount())
                .paymentId(header.getPaymentHeader().getPaymentId())
                .salesId(header.getSalesHeader().getOrderId())
                .items(header.getItems().stream().map(item -> {
                    var salesItem = item.getSalesItem();  // ✅ 지역 변수 선언
                    var paymentItem = item.getPaymentItem();

                    return
                    ReceiptItemDTO.builder()
                            .productName(item.getSalesItem().getProduct().getProductName())
                            .quantity(item.getSalesItem().getSalesQuantity())
                            .unitPrice(item.getSalesItem().getSdPrice())
                            .totalPrice(salesItem.getSdPrice().multiply(
                                    BigDecimal.valueOf(salesItem.getSalesQuantity())
                            ))
                            .paymentMethod(item.getPaymentItem() != null ?
                                    item.getPaymentItem().getPaymentMethod() : null)
                            .build();
                }).toList())
                .build();
    }
}
