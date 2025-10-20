package com.youthcase.orderflow.sd.sdReceipt.controller;

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

    // ✅ 날짜별 조회 (상품 + 결제 내역 포함)
    @GetMapping("/date/{date}")
    public ResponseEntity<List<ReceiptResponse>> getReceiptsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(receiptService.getReceiptsByDate(date));
    }

    // ✅ 영수증 재발행 (50일 제한)
    @GetMapping("/{receiptNo}/reissue")
    public ResponseEntity<ReceiptResponse> reissue(@PathVariable String receiptNo) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(50);

        try {
            ReceiptResponse response = receiptService.getReceiptForReissue(receiptNo, cutoff);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.GONE).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
