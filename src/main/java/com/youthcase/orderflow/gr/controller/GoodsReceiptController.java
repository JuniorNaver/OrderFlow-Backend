package com.youthcase.orderflow.gr.controller;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.gr.dto.GRListDTO;
import com.youthcase.orderflow.gr.dto.GoodsReceiptHeaderDTO;
import com.youthcase.orderflow.gr.dto.POForGRDTO;
import com.youthcase.orderflow.gr.service.GoodsReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gr")
@RequiredArgsConstructor
public class GoodsReceiptController {

    private final GoodsReceiptService service;

    /** ✅ 1. 입고 + 발주 상태 통합 조회 (입고대기 포함) */
    @GetMapping
    public ResponseEntity<List<GRListDTO>> getAllWithPOStatus() {
        return ResponseEntity.ok(service.findAllWithPOStatus());
    }

    /** ✅ 2. 단건 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<GoodsReceiptHeaderDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /** ✅ 3. 등록 */
    @PostMapping
    public ResponseEntity<GoodsReceiptHeaderDTO> create(@RequestBody GoodsReceiptHeaderDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    /** ✅ 4. 입고 확정 (status → RECEIVED) */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirm(@PathVariable Long id) {
        try {
            service.confirmReceipt(id);
            return ResponseEntity.ok(Map.of("message", "입고가 확정되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /** ✅ 5. 입고 취소 (status → CANCELED) */
    @PutMapping("/{poId}/cancel")
    public ResponseEntity<Void> cancelByPoId(
            @PathVariable Long poId,
            @RequestParam(required = false, defaultValue = "no reason") String reason,
            @AuthenticationPrincipal User user
    ) {
        service.cancelByPo(poId, reason, user);
        return ResponseEntity.ok().build();
    }

    /** ✅ 6. 바코드 스캔 → 발주 조회 + 품목 리스트 반환 */
    @GetMapping("/po-search")
    public ResponseEntity<POForGRDTO> searchPOForGR(@RequestParam String barcode) {
        return ResponseEntity.ok(service.searchPOForGR(barcode));
    }

    /** ✅ 7. 스캔 후 바로 입고 생성 + 확정 */
    @PostMapping("/scan-confirm")
    public ResponseEntity<GoodsReceiptHeaderDTO> createAndConfirm(@RequestBody Map<String, Long> req) {
        Long poId = req.get("poId");
        GoodsReceiptHeaderDTO dto = service.createAndConfirmFromPO(poId);
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/search")
    public ResponseEntity<List<GRListDTO>> searchGoodsReceipts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<GRListDTO> results = service.searchGoodsReceipts(query, startDate, endDate);
        return ResponseEntity.ok(results);
    }
}

