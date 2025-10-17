package com.youthcase.orderflow.sd.sdPayment.service;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentMethod;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentStatus;
import com.youthcase.orderflow.sd.sdPayment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.dto.PaymentResult;
import com.youthcase.orderflow.sd.sdPayment.dto.PaymentSplit;
import com.youthcase.orderflow.sd.sdPayment.repository.PaymentHeaderRepository;
import com.youthcase.orderflow.sd.sdPayment.repository.PaymentItemRepository;
import com.youthcase.orderflow.sd.sdPayment.strategy.PaymentStrategy;
import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.repository.SalesHeaderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final Map<String, PaymentStrategy> strategyMap;
    private final PaymentHeaderRepository paymentHeaderRepository;
    private final PaymentItemRepository paymentItemRepository;
    private final SalesHeaderRepository salesHeaderRepository;

    @Override
    @Transactional
    public PaymentResult createPayment(PaymentRequest request) {

        // 주문 조회
        SalesHeader salesHeader = salesHeaderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        // ✅ PaymentHeader 생성
        PaymentHeader header = new PaymentHeader();
        header.setSalesHeader(salesHeader);
        header.setTotalAmount(request.getTotalAmount() != null
                ? request.getTotalAmount()
                : request.getAmount()); // 단일/복수 대응
        header.setPaymentStatus(PaymentStatus.APPROVED);
        paymentHeaderRepository.save(header);

        // ✅ 1️⃣ 혼합 결제 처리
        if (request.getSplits() != null && !request.getSplits().isEmpty()) {
            for (PaymentSplit split : request.getSplits()) {
                processOnePayment(split.getMethod(), split.getAmount(), header, request.getOrderId());
            }
        }
        // ✅ 2️⃣ 단일 결제 처리
        else {
            processOnePayment(request.getPaymentMethod(), request.getAmount(), header, request.getOrderId());
        }

        log.info("✅ 결제 완료 - orderId={}, totalAmount={}",
                request.getOrderId(), header.getTotalAmount());

        return PaymentResult.builder()
                .success(true)
                .message("결제 완료")
                .orderId(request.getOrderId())
                .paidAmount(header.getTotalAmount())
                .build();
    }

    private void processOnePayment(PaymentMethod method, BigDecimal amount, PaymentHeader header, Long orderId) {
        String methodKey = method.getKey().toLowerCase();
        PaymentStrategy strategy = strategyMap.get(methodKey);
        if (strategy == null) {
            throw new IllegalArgumentException("지원하지 않는 결제 수단: " + method);
        }

        // ✅ 개별 결제 수행
        PaymentResult result = strategy.pay(
                PaymentRequest.builder()
                        .orderId(orderId)
                        .amount(amount)
                        .paymentMethod(method)
                        .build()
        );

        if (!result.isSuccess()) {
            throw new IllegalStateException("결제 실패: " + result.getMessage());
        }

        // ✅ PaymentItem 저장
        PaymentItem item = new PaymentItem();
        item.setPaymentHeader(header);
        item.setPaymentMethod(method);
        item.setAmount(amount);
        item.setTransactionNo(result.getTransactionNo());
        item.setPaymentStatus(PaymentStatus.APPROVED);
        paymentItemRepository.save(item);

        log.info("🧾 결제 수단 저장 - method={}, amount={}", method, amount);
    }

    @Override
    public void savePayment(PaymentResult result) {
        // ⚙️ 필요 시 외부 PG Webhook 수신 시 사용할 수 있는 메서드 (선택적)
        SalesHeader salesHeader = salesHeaderRepository.findById(result.getOrderId())
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        PaymentHeader header = new PaymentHeader();
        header.setSalesHeader(salesHeader);
        header.setTotalAmount(result.getPaidAmount());
        header.setPaymentStatus(PaymentStatus.APPROVED);
        paymentHeaderRepository.save(header);

        PaymentItem item = new PaymentItem();
        item.setPaymentHeader(header);
        item.setPaymentMethod(result.getMethod());
        item.setAmount(result.getPaidAmount());
        item.setTransactionNo(result.getTransactionNo());
        item.setPaymentStatus(PaymentStatus.APPROVED);
        paymentItemRepository.save(item);

        log.info("💾 Webhook 결제 데이터 저장 완료 - orderId={}, paymentId={}",
                salesHeader.getOrderId(), header.getPaymentId());
    }

    @Override
    @Transactional
    public PaymentResult cancelPayment(Long paymentItemId) {
        PaymentItem item = paymentItemRepository.findById(paymentItemId)
                .orElseThrow(() -> new RuntimeException("결제 항목을 찾을 수 없습니다."));

        String methodKey = item.getPaymentMethod().getKey().toLowerCase();
        PaymentStrategy strategy = strategyMap.get(methodKey);
        if (strategy == null) {
            throw new IllegalArgumentException("지원하지 않는 결제 수단: " + item.getPaymentMethod());
        }

        strategy.cancel(item);
        item.setPaymentStatus(PaymentStatus.CANCELED);
        paymentItemRepository.save(item);

        return PaymentResult.builder()
                .success(true)
                .message("결제 취소 완료")
                .transactionNo(item.getTransactionNo())
                .method(item.getPaymentMethod())
                .orderId(item.getPaymentHeader().getSalesHeader().getOrderId())
                .paidAmount(item.getAmount())
                .build();
    }



}
