package com.youthcase.orderflow.sd.sdReceipt.controller;

import com.youthcase.orderflow.sd.sdReceipt.dto.ReceiptRequestDTO;
import com.youthcase.orderflow.sd.sdReceipt.dto.ReceiptResponseDTO;
import com.youthcase.orderflow.sd.sdReceipt.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/receipts")
public class ReceiptController {

    private final ReceiptService receiptService;

    /**
     * ✅ 결제 완료 후 영수증 생성
     * 프론트에서 결제 완료 시 자동 호출
     */
    @PostMapping("/{paymentId}")
    public ResponseEntity<ReceiptResponseDTO> createReceipt(@PathVariable Long paymentId) {
        ReceiptResponseDTO response = receiptService.createReceipt(paymentId);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 영수증 단건 조회 (receiptId)
     */
    @GetMapping("/{receiptId}")
    public ResponseEntity<ReceiptResponseDTO> getReceipt(@PathVariable Long receiptId) {
        ReceiptResponseDTO response = receiptService.getReceipt(receiptId);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 결제 ID 기준으로 영수증 조회
     */
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<ReceiptResponseDTO> getReceiptByPayment(@PathVariable Long paymentId) {
        ReceiptResponseDTO response = receiptService.getByPaymentId(paymentId);
        return ResponseEntity.ok(response);
    }
}