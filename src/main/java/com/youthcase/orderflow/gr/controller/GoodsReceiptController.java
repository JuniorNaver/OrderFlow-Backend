package com.youthcase.orderflow.gr.controller;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.gr.dto.GRListDTO;
import com.youthcase.orderflow.gr.dto.GoodsReceiptHeaderDTO;
import com.youthcase.orderflow.gr.dto.POForGRDTO;
import com.youthcase.orderflow.gr.service.GoodsReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    /** ✅ 5. 입고 확정 취소 (status → CANCELED) */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelConfirmed(
            @AuthenticationPrincipal User currentUser,  // ✅ 로그인한 사용자 정보 주입
            @PathVariable Long id,
            @RequestParam(required = false) String reason
    ) {
        try {
            service.cancelConfirmedReceipt(id, reason == null ? "no reason" : reason, currentUser); // ✅ 3번째 인자 전달
            return ResponseEntity.ok(Map.of("message", "입고가 취소되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
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

    /** ✅ 8. 입고 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(Map.of("message", "입고가 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

