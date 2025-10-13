package com.youthcase.orderflow.sd.sdPayment.service;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentStatus;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.payment.dto.PaymentResult;
import com.youthcase.orderflow.sd.sdPayment.repository.PaymentHeaderRepository;
import com.youthcase.orderflow.sd.sdPayment.payment.PaymentProcessor;
import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.repository.SalesHeaderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentHeaderRepository headerRepository;
    private final SalesHeaderRepository salesHeaderRepository;
    private final PaymentProcessor processor;

    @Override
    @Transactional
    public PaymentResult createPayment(PaymentRequest request) {

        // 1️⃣ 주문 확인
        SalesHeader salesHeader = salesHeaderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없음"));

        // 2️⃣ 기존 결제헤더 존재 여부 확인
        PaymentHeader header = headerRepository.findBySalesHeader_OrderId(request.getOrderId())
                .orElseGet(() -> {
                    PaymentHeader newHeader = new PaymentHeader();
                    newHeader.setSalesHeader(salesHeader);
                    newHeader.setTotalAmount(salesHeader.getTotalAmount()); // ✅ 주문 전체 금액 기준
                    newHeader.setPaymentStatus(PaymentStatus.REQUESTED);
                    return newHeader;
                });

        // 3️⃣ 결제 수행
        PaymentResult result = processor.processPayment(request);
        if (!result.isSuccess()) {
            throw new RuntimeException("결제 실패: " + result.getMessage());
        }

        // 4️⃣ PaymentItem 생성 및 누적
        PaymentItem item = new PaymentItem();
        item.setPaymentHeader(header);
        item.setPaymentMethod(request.getPaymentMethod());
        item.setAmount(request.getAmount());
        item.setTransactionNo(result.getTransactionNo());
        header.getPaymentItems().add(item);

        // 5️⃣ 누적 결제금액 계산
        BigDecimal paidTotal = header.getPaymentItems().stream()
                .map(PaymentItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 6️⃣ 상태 업데이트
        if (paidTotal.compareTo(header.getTotalAmount()) >= 0) {
            header.setPaymentStatus(PaymentStatus.APPROVED);
        } else {
            header.setPaymentStatus(PaymentStatus.PARTIALLY_APPROVED); // ✅ 부분결제 상태 추가
        }

        // 7️⃣ 저장
        headerRepository.save(header);

        log.info("💳 결제 누적 상태: {} / {}", paidTotal, header.getTotalAmount());
        return result;
    }

    @Override
    public PaymentHeader getPayment(Long id) {
        return headerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("결제내역 못찾음"));
    }

    @Override
    @Transactional
    public void cancelPayment(Long id) {
        PaymentHeader header = getPayment(id);

        header.getPaymentItems().forEach(item -> {
            processor.cancelPayment(item.getPaymentMethod().getKey(), item);
            item.setTransactionNo(null);
        });

        header.setPaymentStatus(PaymentStatus.CANCELED); // ✅ Enum 사용
        header.setCanceledTime(LocalDateTime.now());
        headerRepository.save(header);
    }
}
