package com.youthcase.orderflow.sd.sdPayment.controller;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdPayment.dto.PaymentRequest;
import com.youthcase.orderflow.sd.sdPayment.dto.PaymentResponse;
import com.youthcase.orderflow.sd.sdPayment.dto.PaymentResult;
import com.youthcase.orderflow.sd.sdPayment.repository.PaymentHeaderRepository;
import com.youthcase.orderflow.sd.sdPayment.repository.PaymentItemRepository;
import com.youthcase.orderflow.sd.sdPayment.service.PaymentProcessor;
import com.youthcase.orderflow.sd.sdPayment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentHeaderRepository paymentHeaderRepository;
    private final PaymentItemRepository paymentItemRepository;
    private final PaymentProcessor paymentProcessor;

    /**
     * 💳 결제 요청 (카드 / 현금 / 간편결제)
     * - paymentMethod: CARD, CASH, EASY
     */
    @PostMapping
    public ResponseEntity<PaymentResult> createPayment(@Valid @RequestBody PaymentRequest request) {
        log.info("💳 [결제요청] method={}, orderId={}, amount={}",
                request.getPaymentMethod(), request.getOrderId(), request.getAmount());

        try {
            // ✅ 결제 로직 전체를 서비스에 위임
            PaymentResult result = paymentService.createPayment(request);

            if (result.isSuccess()) {
                log.info("✅ 결제 성공: {}", result.getTransactionNo());
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
            } else {
                log.warn("❌ 결제 실패: {}", result.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }

        } catch (Exception e) {
            log.error("🔥 결제 처리 중 예외 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(PaymentResult.builder()
                            .success(false)
                            .message("결제 처리 중 오류 발생: " + e.getMessage())
                            .build());
        }
    }


    /**
     * 📄 결제 단건 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
        log.info("🔍 [결제조회] paymentId={}", id);

        PaymentHeader header = paymentHeaderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("결제 내역을 찾을 수 없습니다."));

        return ResponseEntity.ok(PaymentResponse.from(header));
    }

    /**
     * ❌ 결제 취소 요청
     * - 카드/현금/간편결제 모두 전략에 맞게 처리
     */
    @PostMapping("/{itemId}/cancel")
    public ResponseEntity<PaymentResult> cancelPayment(@PathVariable Long itemId) {
        log.info("🧾 [결제취소요청] paymentItemId={}", itemId);

        PaymentItem item = paymentItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("결제 항목을 찾을 수 없습니다."));

        try {
            paymentProcessor.cancelPayment(item.getPaymentMethod().getKey(), item);
            log.info("✅ 결제 취소 완료 - method={}, transactionNo={}",
                    item.getPaymentMethod(), item.getTransactionNo());

            return ResponseEntity.ok(PaymentResult.builder()
                    .success(true)
                    .message("결제 취소 완료")
                    .transactionNo(item.getTransactionNo())
                    .method(item.getPaymentMethod())
                    .orderId(item.getPaymentHeader().getSalesHeader().getOrderId())
                    .paidAmount(item.getAmount())
                    .build());

        } catch (Exception e) {
            log.error("❌ 결제 취소 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(PaymentResult.builder()
                            .success(false)
                            .message("결제 취소 실패: " + e.getMessage())
                            .build());
        }
    }

    /**
     * 📡 아임포트 웹훅 수신 (선택)
     * - 아임포트 관리자 설정 시 자동 호출
     * - imp_uid만 전달되므로, 서버에서 재검증 필요
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestParam("imp_uid") String impUid) {
        log.info("📡 [Webhook 수신] imp_uid={}", impUid);
        // TODO: EasyPaymentService.verifyWebhook(impUid) 구현 시, 상태 동기화 가능
        return ResponseEntity.ok().build();
    }
}