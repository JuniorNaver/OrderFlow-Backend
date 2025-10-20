package com.youthcase.orderflow.sd.sdReceipt.controller;

import com.youthcase.orderflow.sd.sdReceipt.domain.Receipt;
import com.youthcase.orderflow.sd.sdReceipt.dto.ReceiptResponse;
import com.youthcase.orderflow.sd.sdReceipt.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @GetMapping("/date/{date}")
    public ResponseEntity<List<ReceiptResponse>> getReceiptsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(receiptService.getReceiptsByDate(date));
    }

    @GetMapping("/{receiptNo}/reissue")
    public ResponseEntity<ReceiptResponse> reissue(@PathVariable String receiptNo) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(50);
        Receipt receipt = receiptService.findByReceiptNo(receiptNo);
        if (receipt.getIssuedAt().isBefore(cutoff)) {
            return ResponseEntity.status(HttpStatus.GONE).body(null);
        }
        return ResponseEntity.ok(ReceiptResponse.fromEntity(receipt));
    }
}