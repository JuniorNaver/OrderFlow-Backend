package com.youthcase.orderflow.sd.sdRefund.controller;

import com.youthcase.orderflow.sd.sdReceipt.dto.ReceiptResponse;
import com.youthcase.orderflow.sd.sdReceipt.repository.ReceiptRepository;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundStatus;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundRequest;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundResponse;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundVerifyRequest;
import com.youthcase.orderflow.sd.sdRefund.dto.VerifyRefundResponse;
import com.youthcase.orderflow.sd.sdRefund.repository.RefundHeaderRepository;
import com.youthcase.orderflow.sd.sdRefund.service.RefundIamportService;
import com.youthcase.orderflow.sd.sdRefund.service.RefundProcessor;
import com.youthcase.orderflow.sd.sdPayment.repository.PaymentHeaderRepository;
import com.youthcase.orderflow.sd.sdPayment.repository.PaymentItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {
    private final RefundProcessor refundProcessor;
    private final RefundIamportService refundIamportService;
    private final RefundHeaderRepository refundHeaderRepository;
    private final ReceiptRepository receiptRepository;

        // 🔧 추가 주입 (검증용)
        private final PaymentHeaderRepository paymentHeaderRepository;
        private final PaymentItemRepository paymentItemRepository;

        /**
         * ✅ (통합) 환불 사전 검증
         * - EASY: impUid로 PG 검증
         * - CARD: transactionNo로 내부 결제항목 존재여부 검증
         * - CASH: receiptNo로 영수증 존재여부 검증
         */
        @PostMapping("/verify")
        public ResponseEntity<?> verify(@RequestBody RefundVerifyRequest req) {
            final String method = req.getPaymentMethod();
            log.info("🔍 환불 검증 요청: method={}, impUid={}, txNo={}, receiptNo={}",
                    method, req.getImpUid(), req.getTransactionNo(), req.getReceiptNo());

            switch (method) {
                case "EASY" -> {
                    if (req.getImpUid() == null || req.getImpUid().isBlank()) {
                        return ResponseEntity.badRequest().body(Map.of("message","impUid가 필요합니다."));
                    }
                    VerifyRefundResponse res = refundIamportService.verifyPayment(req.getImpUid());
                    return ResponseEntity.ok(res);
                }
                case "CARD" -> {
                    if (req.getTransactionNo() == null || req.getTransactionNo().isBlank()) {
                        return ResponseEntity.badRequest().body(Map.of("message","transactionNo가 필요합니다."));
                    }
                    boolean exists = paymentItemRepository.existsByTransactionNo(req.getTransactionNo());
                    return exists ? ResponseEntity.ok(Map.of("result","OK"))
                            : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","거래번호 없음"));
                }
                case "CASH" -> {
                    if (req.getReceiptNo() == null || req.getReceiptNo().isBlank()) {
                        return ResponseEntity.badRequest().body(Map.of("message","receiptNo가 필요합니다."));
                    }
                    boolean exists = receiptRepository.existsByReceiptNo(req.getReceiptNo());
                    return exists ? ResponseEntity.ok(Map.of("result","OK"))
                            : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","영수증 없음"));
                }
                default -> {
                    return ResponseEntity.badRequest().body(Map.of("message","지원하지 않는 결제수단"));
                }
            }
        }

        /**
         * ✅ 환불 실행
         * - 반드시 paymentId를 받아 PaymentHeader를 연결한 RefundHeader를 생성
         * - RefundProcessor(Strategy) 통해 결제수단별 환불 실행
         */
            @PostMapping
            @Transactional
            public ResponseEntity<RefundResponse> refund(@RequestBody RefundRequest request) {
                log.info("💳 환불 요청 시작: paymentId={}, amount={}, reason={}",
                        request.getPaymentId(), request.getCancelAmount(), request.getReason());

                var paymentHeader = paymentHeaderRepository.findById(request.getPaymentId())
                        .orElseThrow(() -> new IllegalArgumentException("결제 내역이 존재하지 않습니다."));

                RefundHeader header = RefundHeader.builder()
                        .paymentHeader(paymentHeader) // ✅ FK 필수 세팅
                        .refundAmount(BigDecimal.valueOf(request.getCancelAmount()))
                        .reason(request.getReason())
                        .refundStatus(RefundStatus.REQUESTED)
                        .build();

                refundHeaderRepository.save(header);

                RefundResponse response = refundProcessor.processRefund(header);

                log.info("✅ 환불 처리 완료: refundId={}, status={}", response.getRefundId(), response.getRefundStatus());
                return ResponseEntity.ok(response);
            }

            // ✅ 특정 환불건 상세 조회
            @GetMapping("/{refundId}")
            public ResponseEntity<RefundResponse> getRefundDetail(@PathVariable Long refundId) {
                var header = refundHeaderRepository.findById(refundId)
                        .orElseThrow(() -> new IllegalArgumentException("해당 환불 내역이 존재하지 않습니다."));

                RefundResponse response = RefundResponse.builder()
                        .refundId(header.getRefundId())
                        .paymentId(header.getPaymentHeader().getPaymentId())
                        .refundAmount(header.getRefundAmount().doubleValue())
                        .refundStatus(header.getRefundStatus())
                        .reason(header.getReason())
                        .requestedTime(header.getRequestedTime())
                        .approvedTime(header.getApprovedTime())
                        .build();
                return ResponseEntity.ok(response);
            }

        // ✅ 영수증 번호로 환불 대상 조회
        @GetMapping("/receipt/{receiptNo}")
        public ResponseEntity<?> getRefundTarget(@PathVariable String receiptNo) {
            var receipt = receiptRepository.findByReceiptNo(receiptNo)
                    .orElseThrow(() -> new IllegalArgumentException("해당 영수증을 찾을 수 없습니다."));
            var dto = ReceiptResponse.fromEntity(receipt);
            log.info("✅ 환불 대상 조회 완료 - {}", receiptNo);
            return ResponseEntity.ok(dto);
        }
    }