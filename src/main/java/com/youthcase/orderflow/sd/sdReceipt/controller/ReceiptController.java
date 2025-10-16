package com.youthcase.orderflow.sd.sdReceipt.controller;

import com.youthcase.orderflow.sd.sdReceipt.domain.Receipt;
import com.youthcase.orderflow.sd.sdReceipt.dto.ReceiptResponse;
import com.youthcase.orderflow.sd.sdReceipt.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    // ✅ 날짜별 영수증 조회
    @GetMapping("/date/{date}")
    public ResponseEntity<List<ReceiptResponse>> getReceiptsByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(receiptService.getReceiptsByDate(date));
    }

    // ✅ 재발행 (receiptNo 기준으로 PDF나 화면용 데이터 리턴)
    @GetMapping("/{receiptNo}/reissue")
    public ResponseEntity<ReceiptResponse> reissue(@PathVariable String receiptNo) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(50);
        Receipt receipt = receiptService.findByReceiptNo(receiptNo);
        if (receipt.getIssuedAt().isBefore(cutoff)) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .body(null); // 410 Gone → 재발행 불가
        }
        return ResponseEntity.ok(ReceiptResponse.fromEntity(receipt));
    }
}
