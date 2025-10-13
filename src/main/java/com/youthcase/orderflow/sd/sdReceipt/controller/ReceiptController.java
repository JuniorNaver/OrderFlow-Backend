package com.youthcase.orderflow.sd.sdReceipt.controller;

import com.youthcase.orderflow.sd.sdReceipt.dto.ReceiptResponseDTO;
import com.youthcase.orderflow.sd.sdReceipt.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/receipt")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    // 결제 완료 후 영수증 생성
    @PostMapping("/{paymentId}")
    public ResponseEntity<ReceiptResponseDTO> create(@PathVariable Long paymentId) {
        return ResponseEntity.ok(receiptService.createReceipt(paymentId));
    }

    // paymentId로 영수증 조회
    @GetMapping("/by-payment/{paymentId}")
    public ResponseEntity<ReceiptResponseDTO> getByPayment(@PathVariable Long paymentId) {
        return ResponseEntity.ok(receiptService.getReceiptByPaymentId(paymentId));
    }

    // receiptId로 영수증 조회
    @GetMapping("/{receiptId}")
    public ResponseEntity<ReceiptResponseDTO> get(@PathVariable Long receiptId) {
        return ResponseEntity.ok(receiptService.getReceipt(receiptId));
    }
}
