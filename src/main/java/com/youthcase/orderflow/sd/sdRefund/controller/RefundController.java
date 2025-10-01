package com.youthcase.orderflow.sd.sdRefund.controller;

import com.youthcase.orderflow.sd.sdRefund.refund.dto.RefundRequestDTO;
import com.youthcase.orderflow.sd.sdRefund.refund.dto.RefundResponseDTO;
import com.youthcase.orderflow.sd.sdRefund.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    // 환불 등록
    @PostMapping
    public ResponseEntity<RefundResponseDTO> createRefund(@RequestBody RefundRequestDTO requestDTO) {
        RefundResponseDTO response = refundService.createRefund(requestDTO);
        return ResponseEntity.ok(response);
    }

    // 환불 조회
    @GetMapping("/{refundId}")
    public ResponseEntity<RefundResponseDTO> getRefund(@PathVariable Long refundId) {
        RefundResponseDTO response = refundService.getRefund(refundId);
        return ResponseEntity.ok(response);
    }
}