package com.youthcase.orderflow.gr.controller;

import com.youthcase.orderflow.gr.dto.GoodsReceiptHeaderDTO;
import com.youthcase.orderflow.gr.dto.POForGRDTO;
import com.youthcase.orderflow.gr.service.GoodsReceiptService;
import com.youthcase.orderflow.po.dto.POHeaderResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gr")
@RequiredArgsConstructor
public class GoodsReceiptController {

    private final GoodsReceiptService service;

    /** ✅ 1. 전체 조회 */
    @GetMapping
    public ResponseEntity<List<GoodsReceiptHeaderDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
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

    /** ✅ 4. 입고 확정 */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable Long id) {
        service.confirmReceipt(id);
        return ResponseEntity.ok().build();
    }

    /** ✅ 5. 입고 확정 취소 */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id, @RequestParam(required = false) String reason) {
        service.cancelConfirmedReceipt(id, reason == null ? "no reason" : reason);
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


}
