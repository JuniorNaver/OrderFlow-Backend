package com.youthcase.orderflow.sd.sdRefund.controller;

import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdRefund.dto.CancelRequest;
import com.youthcase.orderflow.sd.sdRefund.dto.RefundResponse;
import com.youthcase.orderflow.sd.sdRefund.dto.VerifyRefundResponse;
import com.youthcase.orderflow.sd.sdRefund.repository.RefundHeaderRepository;
import com.youthcase.orderflow.sd.sdRefund.service.RefundIamportService;
import com.youthcase.orderflow.sd.sdRefund.service.RefundProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundProcessor refundProcessor;
    private final RefundIamportService refundIamportService;
    private final RefundHeaderRepository refundHeaderRepository;

    // ✅ 1️⃣ PG 결제 검증 (결제 상태 확인)
    @GetMapping("/verify/{impUid}")
    public ResponseEntity<VerifyRefundResponse> verifyPayment(@PathVariable String impUid) {
        log.info("🔍 PG 결제 검증 요청: impUid={}", impUid);

        VerifyRefundResponse response = refundIamportService.verifyPayment(impUid);
        log.info("✅ PG 검증 완료: status={}, amount={}", response.status(), response.cancelAmount());

        return ResponseEntity.ok(response);
    }

    // ✅ 2️⃣ 환불 요청 (PG + 내부 로직 통합)
    @PostMapping
    public ResponseEntity<RefundResponse> refund(@RequestBody CancelRequest request) {
        log.info("💳 환불 요청 시작: impUid={}, amount={}, reason={}",
                request.impUid(), request.cancelAmount(), request.reason());

        // 1️⃣ 우선 DB에서 RefundHeader 생성 (실제 시스템에서는 생성 or 조회)
        RefundHeader header = RefundHeader.builder()
                .reason(request.reason())
                .refundAmount(java.math.BigDecimal.valueOf(request.cancelAmount()))
                .build();

        refundHeaderRepository.save(header);

        // 2️⃣ RefundProcessor 호출 (내부 전략 + PG 연동)
        RefundResponse response = refundProcessor.processRefund(header);

        log.info("✅ 환불 처리 완료: refundId={}, status={}", response.getRefundId(), response.getRefundStatus());
        return ResponseEntity.ok(response);
    }

    // ✅ 3️⃣ 특정 환불건 상세 조회
    @GetMapping("/{refundId}")
    public ResponseEntity<RefundResponse> getRefundDetail(@PathVariable Long refundId) {
        log.info("📄 환불 상세조회 요청: refundId={}", refundId);

        RefundHeader header = refundHeaderRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("해당 환불 내역이 존재하지 않습니다."));

        RefundResponse response = RefundResponse.builder()
                .refundId(header.getRefundId())
                .refundAmount(header.getRefundAmount().doubleValue())
                .refundStatus(header.getRefundStatus())
                .reason(header.getReason())
                .requestedTime(header.getRequestedTime())
                .approvedTime(header.getApprovedTime())
                .build();

        return ResponseEntity.ok(response);
    }
}