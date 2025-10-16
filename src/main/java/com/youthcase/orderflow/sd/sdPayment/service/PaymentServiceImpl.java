package com.youthcase.orderflow.sd.sdPayment.service;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentStatus;
import com.youthcase.orderflow.sd.sdPayment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.dto.PaymentResult;
import com.youthcase.orderflow.sd.sdPayment.repository.PaymentHeaderRepository;
import com.youthcase.orderflow.sd.sdPayment.repository.PaymentItemRepository;
import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.repository.SalesHeaderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentProcessor paymentProcessor;
    private final PaymentHeaderRepository paymentHeaderRepository;
    private final SalesHeaderRepository salesHeaderRepository;
    private final PaymentItemRepository paymentItemRepository;

    @Transactional
    @Override
    public PaymentResult createPayment(PaymentRequest request) {

        // 1️⃣ 실제 결제 프로세스 실행 (Mock 또는 PG)
        PaymentResult result = paymentProcessor.processPayment(request);

        if (!result.isSuccess()) {
            throw new IllegalStateException("결제 실패: " + result.getMessage());
        }

        // 2️⃣ 주문 조회
        SalesHeader salesHeader = salesHeaderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        // 3️⃣ PaymentHeader 생성 및 저장
        PaymentHeader paymentHeader = new PaymentHeader();
        paymentHeader.setSalesHeader(salesHeader); // FK 연결 ✅
        paymentHeader.setTotalAmount(request.getAmount());
        paymentHeader.setPaymentStatus(PaymentStatus.APPROVED);
        paymentHeaderRepository.save(paymentHeader);

        // 4️⃣ PaymentItem 생성 및 저장 (결제 수단 단위)
        PaymentItem item = new PaymentItem();
        item.setPaymentHeader(paymentHeader);
        item.setPaymentMethod(request.getPaymentMethod());
        item.setAmount(request.getAmount());
        paymentItemRepository.save(item);

        log.info("✅ 결제 저장 완료 - 주문ID: {}, 결제ID: {}", salesHeader.getOrderId(), paymentHeader.getPaymentId());

        return result;
    }

    @Override
    @Transactional
    public void savePayment(PaymentResult result) {
        // 🔹 orderId 기준으로 SalesHeader 조회
        SalesHeader salesHeader = salesHeaderRepository.findById(result.getOrderId())
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        // 🔹 PaymentHeader 생성
        PaymentHeader header = new PaymentHeader();
        header.setSalesHeader(salesHeader);
        header.setTotalAmount(result.getPaidAmount());
        header.setPaymentStatus(PaymentStatus.APPROVED);
        paymentHeaderRepository.save(header);

        // 🔹 PaymentItem 생성
        PaymentItem item = new PaymentItem();
        item.setPaymentHeader(header);
        item.setPaymentMethod(result.getMethod());
        item.setAmount(result.getPaidAmount());
        item.setTransactionNo(result.getTransactionNo());
        item.setPaymentStatus(PaymentStatus.APPROVED);
        paymentItemRepository.save(item);

        log.info("💾 결제 데이터 저장 완료 - orderId={}, paymentId={}",
                salesHeader.getOrderId(), header.getPaymentId());
    }
}
