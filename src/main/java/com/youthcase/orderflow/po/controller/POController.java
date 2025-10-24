package com.youthcase.orderflow.po.controller;

import com.youthcase.orderflow.po.dto.POHeaderResponseDTO;
import com.youthcase.orderflow.po.dto.POItemRequestDTO;
import com.youthcase.orderflow.po.dto.POItemResponseDTO;
import com.youthcase.orderflow.po.service.POService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/po")
@RequiredArgsConstructor
public class POController {

    private final POService poService;

    /** ✅ POHeader만 생성 */
    @PostMapping("")
    public ResponseEntity<POHeaderResponseDTO> createPOHeader() {
        POHeaderResponseDTO response = poService.createNewPOHeader();
        return ResponseEntity.ok(response);
    }

    /** ✅ 기존 헤더에 아이템 추가 */
    @PostMapping("/{poId}/items")
    public ResponseEntity<POItemResponseDTO> addPOItem(
            @PathVariable Long poId,
            @RequestBody POItemRequestDTO dto,
            @RequestParam String gtin
    ) {
        POItemResponseDTO response = poService.addPOItem(poId, dto, gtin);
        return ResponseEntity.ok(response);
    }





    /** ✅ 장바구니 조회 */
    @GetMapping("/items")
    public ResponseEntity<List<POItemResponseDTO>> getAllItems(@RequestParam Long poId) {
        return ResponseEntity.ok(poService.getAllItems(poId));
    }

    /** ✅ 수량 변경 */
    @PutMapping("/update/{itemNo}")
    public ResponseEntity<POItemResponseDTO> updateItemQuantity(
            @PathVariable Long itemNo,
            @RequestBody POItemRequestDTO dto
    ) {
        return ResponseEntity.ok(poService.updateItemQuantity(itemNo, dto));
    }

    /** ✅ 상품 삭제 */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteItem(@RequestParam List<Long> itemIds) {
        poService.deleteItem(itemIds);
        return ResponseEntity.noContent().build();
    }

    /** ✅ 장바구니 저장 */
    @PostMapping("/save/{poId}")
    public ResponseEntity<Void> saveCart(
            @PathVariable Long poId,
            @RequestBody Map<String, String> body
    ) {
        String remarks = body.get("remarks");
        poService.saveCart(poId, remarks);
        return ResponseEntity.ok().build();
    }

    /** ✅ 저장된 장바구니 목록 */
    @GetMapping("/saved")
    public ResponseEntity<List<POHeaderResponseDTO>> getSavedCartList() {
        return ResponseEntity.ok(poService.getSavedCartList());
    }

    /** ✅ 특정 장바구니 불러오기 */
    @GetMapping("/savedCart/{poId}")
    public ResponseEntity<List<POItemResponseDTO>> getSavedCart(@PathVariable Long poId) {
        return ResponseEntity.ok(poService.getSavedCartItems(poId));
    }

    /** ✅ 저장한 장바구니 삭제 */
    @DeleteMapping("/delete/{poId}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long poId) {
        poService.deletePO(poId);
        return ResponseEntity.noContent().build();
    }

    /** ✅ 발주 확정 */
    @PostMapping("/confirm/{poId}")
    public ResponseEntity<Void> confirmOrder(@PathVariable Long poId) {
        poService.confirmOrder(poId);
        return ResponseEntity.ok().build();
    }

    /** ✅ 입고 진행률 업데이트 (임시) */
    @PutMapping("/progress/{poId}")
    public ResponseEntity<Void> updateReceiveProgress(@PathVariable Long poId) {
        poService.updateReceiveProgress(poId);
        return ResponseEntity.ok().build();
    }
}