package com.youthcase.orderflow.sd.sdPayment.service;

import com.youthcase.orderflow.master.store.domain.Store;
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
import com.youthcase.orderflow.sd.sdReceipt.service.ReceiptService;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
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

    private final ReceiptService receiptService;

    @Override
    @Transactional
    public PaymentResult createPayment(PaymentRequest request) {
        try {
            // ✅ 주문 조회
            SalesHeader salesHeader = salesHeaderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

            // ✅ PaymentHeader 생성
            PaymentHeader header = new PaymentHeader();
            header.setSalesHeader(salesHeader);
            header.setTotalAmount(
                    request.getTotalAmount() != null
                            ? request.getTotalAmount()
                            : request.getAmount()
            );
            header.setPaymentStatus(PaymentStatus.APPROVED);
            paymentHeaderRepository.save(header);

            // ✅ 결제 처리 (혼합결제 vs 단일결제)
            if (request.getSplits() != null && !request.getSplits().isEmpty()) {
                for (PaymentSplit split : request.getSplits()) {
                    processOnePayment(request, split.getMethod(), split.getAmount(), header, request.getOrderId());
                }
            } else {
                processOnePayment(request, request.getPaymentMethod(), request.getAmount(), header, request.getOrderId());
            }

            log.info("✅ 결제 완료 - orderId={}, totalAmount={}", request.getOrderId(), header.getTotalAmount());

            // ✅ 영수증 생성 추가
            Store store = salesHeader.getStore(); // Store 가져오기
            receiptService.createReceipt(
                    salesHeader,
                    header,
                    (RefundHeader) null,  // 환불 아닐 경우 null
                    store
            );

            log.info("🧾 영수증 생성 완료 - salesId={}, paymentId={}",
                    salesHeader.getOrderNo(), header.getPaymentId());

            return PaymentResult.builder()
                    .success(true)
                    .message("결제 완료 및 영수증 생성됨")
                    .orderId(request.getOrderId())
                    .paidAmount(header.getTotalAmount())
                    .build();

        } catch (Exception e) {
            log.error("❌ 결제 중 오류 발생: {}", e.getMessage(), e);
            return PaymentResult.builder()
                    .success(false)
                    .message("결제 실패: " + e.getMessage())
                    .orderId(request.getOrderId())
                    .build();
        }
    }

    /**
     * ✅ 개별 결제 수행 메서드 (카드/현금/간편결제)
     */
    private void processOnePayment(
            PaymentRequest request,
            PaymentMethod method,
            BigDecimal amount,
            PaymentHeader header,
            Long orderId
    ) {
        String methodKey = method.getKey().toLowerCase();
        PaymentStrategy strategy = strategyMap.get(methodKey);

        if (strategy == null) {
            throw new IllegalArgumentException("지원하지 않는 결제 수단: " + method);
        }

        // ✅ impUid 보정 로직 추가
        String impUid = request.getImpUid();
        if (impUid == null || impUid.isBlank()) {
            // 테스트 모드 또는 mock 환경 대응
            impUid = "IMP_TEST_" + System.currentTimeMillis();
            log.warn("⚠️ impUid 비어있음 → 테스트용 impUid로 대체: {}", impUid);
        }

        // ✅ 새로운 PaymentRequest 빌드 시 보정된 impUid 사용
        PaymentRequest safeRequest = PaymentRequest.builder()
                .orderId(orderId)
                .amount(amount)
                .paymentMethod(method)
                .impUid(impUid)
                .merchantUid(request.getMerchantUid())
                .provider(request.getProvider())
                .transactionNo(request.getTransactionNo())
                .build();

        // ✅ 개별 결제 실행
        PaymentResult result = strategy.pay(safeRequest);

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

        header.getPaymentItems().add(item);

        log.info("🧾 결제 수단 저장 완료 - method={}, amount={}, impUid={}", method, amount, impUid);
    }

    @Override
    public void savePayment(PaymentResult result) {
        // ⚙️ 외부 PG(Webhook) 수신 시 결제 데이터 저장용
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

        // ✅ 결제 취소 처리
        strategy.cancel(item);
        item.setPaymentStatus(PaymentStatus.CANCELED);
        paymentItemRepository.save(item);

        log.info("🧾 결제 취소 완료 - transactionNo={}", item.getTransactionNo());

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