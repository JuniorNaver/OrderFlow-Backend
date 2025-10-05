package com.youthcase.orderflow.sd.sdRefund.controller;

import com.youthcase.orderflow.sd.sdRefund.domain.RefundStatus;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundRequestDTO;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundResponseDTO;
import com.youthcase.orderflow.sd.sdRefund.service.RefundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    /**
     * ✅ 1️⃣ 환불 생성
     * POST /api/refunds
     */
    @PostMapping
    public ResponseEntity<RefundResponseDTO> createRefund(@RequestBody RefundRequestDTO request) {
        log.info("💳 [환불 요청] paymentId={}, reason={}", request.getPaymentId(), request.getReason());
        RefundResponseDTO response = refundService.createRefund(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * ✅ 2️⃣ 환불 상태별 조회
     * GET /api/refunds/status?status=REQUESTED
     */
    @GetMapping("/status")
    public ResponseEntity<List<RefundResponseDTO>> getRefundsByStatus(@RequestParam RefundStatus status) {
        log.info("🔍 [상태별 환불 조회] status={}", status);
        List<RefundResponseDTO> refunds = refundService.getRefundsByStatus(status);
        return ResponseEntity.ok(refunds);
    }

    /**
     * ✅ 3️⃣ 특정 결제건 환불 내역 조회
     * GET /api/refunds/payment/{paymentId}
     */
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<List<RefundResponseDTO>> getRefundsByPaymentId(@PathVariable Long paymentId) {
        log.info("🔍 [결제건별 환불 조회] paymentId={}", paymentId);
        List<RefundResponseDTO> refunds = refundService.getRefundsByPaymentId(paymentId);
        return ResponseEntity.ok(refunds);
    }

    /**
     * ✅ 4️⃣ 기간별 환불 조회 (정산/관리자용)
     * GET /api/refunds/period?start=2025-10-01T00:00:00&end=2025-10-05T23:59:59
     */
    @GetMapping("/period")
    public ResponseEntity<List<RefundResponseDTO>> getRefundsBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        log.info("📆 [기간별 환불 조회] start={}, end={}", start, end);
        List<RefundResponseDTO> refunds = refundService.getRefundsBetween(start, end);
        return ResponseEntity.ok(refunds);
    }

    /**
     * ✅ 5️⃣ 기본 연결 테스트용 (optional)
     * GET /api/refunds/ping
     */
    @GetMapping("/ping")
    public String ping() {
        return "Refund API 연결 성공 ✅";
    }
}