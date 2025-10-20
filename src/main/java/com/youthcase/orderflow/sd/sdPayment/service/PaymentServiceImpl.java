package com.youthcase.orderflow.sd.sdPayment.service;

import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.sd.sdPayment.domain.*;
import com.youthcase.orderflow.sd.sdPayment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.dto.PaymentResult;
import com.youthcase.orderflow.sd.sdPayment.dto.PaymentSplit;
import com.youthcase.orderflow.sd.sdPayment.repository.PaymentHeaderRepository;
import com.youthcase.orderflow.sd.sdPayment.repository.PaymentItemRepository;
import com.youthcase.orderflow.sd.sdPayment.strategy.PaymentStrategy;
import com.youthcase.orderflow.sd.sdReceipt.service.ReceiptService;
import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.domain.SalesStatus;
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
            // ✅ 1. 주문 조회
            SalesHeader salesHeader = salesHeaderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

            // ✅ 2. PaymentHeader 생성
            PaymentHeader header = new PaymentHeader();
            header.setSalesHeader(salesHeader);
            header.setPaymentStatus(PaymentStatus.APPROVED);
            header.setTotalAmount(BigDecimal.ZERO);
            paymentHeaderRepository.save(header);

            // ✅ 3. 결제 처리 (단일/분할 모두 지원)
            BigDecimal totalPaid = BigDecimal.ZERO;
            if (request.getSplits() != null && !request.getSplits().isEmpty()) {
                for (PaymentSplit split : request.getSplits()) {
                    processOnePayment(request, split.getMethod(), split.getAmount(), header, request.getOrderId());
                    totalPaid = totalPaid.add(split.getAmount());
                }
            } else {
                processOnePayment(request, request.getPaymentMethod(), request.getAmount(), header, request.getOrderId());
                totalPaid = totalPaid.add(request.getAmount());
            }

            // ✅ 4. 결제 금액 반영
            header.setTotalAmount(totalPaid);
            paymentHeaderRepository.saveAndFlush(header);

            // ✅ 5. 재고 차감 및 판매 확정
            salesHeader.getSalesItems().forEach(item -> {
                var stk = item.getStk();
                if (stk != null) {
                    int newQty = Math.max(stk.getQuantity() - item.getSalesQuantity(), 0);
                    stk.setQuantity(newQty);
                }
            });
            salesHeader.setSalesStatus(SalesStatus.COMPLETED);
            salesHeaderRepository.save(salesHeader);

            // ✅ 6. 모든 변경사항을 DB에 반영 (중요!)
            paymentItemRepository.flush();
            paymentHeaderRepository.flush();
            salesHeaderRepository.flush(); // 🔥 이거 없으면 SalesItem이 아직 DB에 없음

            // ✅ 7. 영수증 생성
            try {
                Store store = salesHeader.getStore();
                if (store == null) {
                    throw new IllegalStateException("❌ 영수증 생성 실패: 매장 정보가 없습니다.");
                }

                salesHeaderRepository.flush();
                paymentHeaderRepository.flush();

                receiptService.createUnifiedReceipt(salesHeader, store);
                log.info("🧾 영수증 생성 완료 - orderNo={}, total={}, items={}",
                        salesHeader.getOrderNo(),
                        totalPaid,
                        salesHeader.getSalesItems().size());
            } catch (Exception e) {
                log.error("❌ 영수증 생성 중 오류 발생: {}", e.getMessage(), e);
            }

            // ✅ 8. 성공 결과 반환
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
     * ✅ 개별 결제 단위 처리
     */
    private void processOnePayment(
            PaymentRequest request,
            PaymentMethod method,
            BigDecimal amount,
            PaymentHeader header,
            Long orderId
    ) {
        // 전략 선택
        String methodKey = method.getKey().toLowerCase();
        PaymentStrategy strategy = strategyMap.get(methodKey);
        if (strategy == null) {
            throw new IllegalArgumentException("지원하지 않는 결제 수단: " + method);
        }

        // impUid 보정
        String impUid = request.getImpUid();
        if (impUid == null || impUid.isBlank()) {
            impUid = "IMP_TEST_" + System.currentTimeMillis();
            log.warn("⚠️ impUid 비어있음 → 테스트용 impUid로 대체: {}", impUid);
        }

        // 안전한 요청 복제
        PaymentRequest safeRequest = PaymentRequest.builder()
                .orderId(orderId)
                .amount(amount)
                .paymentMethod(method)
                .impUid(impUid)
                .merchantUid(request.getMerchantUid())
                .provider(request.getProvider())
                .transactionNo(request.getTransactionNo())
                .build();

        // ✅ 결제 실행
        PaymentResult result = strategy.pay(safeRequest);
        if (!result.isSuccess()) {
            throw new IllegalStateException("결제 실패: " + result.getMessage());
        }

        // ✅ 결제 항목 저장
        PaymentItem item = new PaymentItem();
        item.setPaymentHeader(header);
        item.setPaymentMethod(method);
        item.setAmount(amount);
        item.setTransactionNo(result.getTransactionNo());
        item.setPaymentStatus(PaymentStatus.APPROVED);
        paymentItemRepository.save(item);
        header.getPaymentItems().add(item);

        log.info("💳 결제 완료 - method={}, amount={}, txNo={}", method, amount, result.getTransactionNo());
    }

    /**
     * ✅ 외부 PG(Webhook) 수신 시 결제 데이터 저장
     */
    @Override
    public void savePayment(PaymentResult result) {
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

    /**
     * ✅ 결제 취소 처리
     */
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
