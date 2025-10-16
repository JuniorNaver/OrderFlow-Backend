package com.youthcase.orderflow.po.controller;

import com.youthcase.orderflow.po.domain.POStatus;
import com.youthcase.orderflow.po.dto.POHeaderResponseDTO;
import com.youthcase.orderflow.po.dto.POItemRequestDTO;
import com.youthcase.orderflow.po.dto.POItemResponseDTO;
import com.youthcase.orderflow.po.service.POHeaderService;
import com.youthcase.orderflow.po.service.POItemService;
import com.youthcase.orderflow.po.service.POService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/po")
public class POController {

    private final POHeaderService poHeaderService;
    private final POItemService poItemService;
    private final POService poService;

    public POController(POHeaderService poHeaderService, POItemService poItemService, POService poService) {
        this.poHeaderService = poHeaderService;
        this.poItemService = poItemService;
        this.poService = poService;
    }

    /** 장바구니 PO 생성 */
    @PostMapping("") // POST /api/po
    public ResponseEntity<Map<String, Long>> createPO() {
        Long poId = poHeaderService.createNewPO(); // status = PR
        return ResponseEntity.ok(Map.of("poId", poId));
    }

    /** 장바구니 상품 조회: 헤더id와 상태 기준으로 아이템 목록 찾아오기 */
    @GetMapping("/items")
    public List<POItemResponseDTO> getAllItems(Long poId, POStatus status) {
        return poItemService.getAllItems(poId, status);
    }




    /** 상품 수량 변경 */
    @PutMapping("/update/{itemNo}")
    public POItemResponseDTO updateItemQuantity(@PathVariable Long itemNo, @RequestBody POItemRequestDTO dto) {
        return poItemService.updateItemQuantity(itemNo, dto);
    }
    /** 상품 삭제  */
    @DeleteMapping("/delete")
    public void deleteItem(@RequestParam List<Long> itemIds) {
        poItemService.deleteItem(itemIds);
    }




    /** 장바구니 저장 */
    @PostMapping("/{poId}/save")
    public ResponseEntity<String> saveCart(@PathVariable Long poId) {
        poHeaderService.updateStatusToSaved(poId);
        return ResponseEntity.ok("장바구니 저장 완료");
    }
    /** 장바구니 목록 불러오기 */
    @GetMapping("/saved")
    public ResponseEntity<List<POHeaderResponseDTO>> getSavedCartList() {
        List<POHeaderResponseDTO> savedList = poHeaderService.getSavedCartList();
        return ResponseEntity.ok(savedList);
    }
    /** 특정 장바구니 불러오기 */
    @GetMapping("/{poId}/savedCart")
    public ResponseEntity<List<POItemResponseDTO>> getSavedCart(@PathVariable Long poId) {
        List<POItemResponseDTO> savedItems = poItemService.getSavedCartItems(poId);
        return ResponseEntity.ok(savedItems);
    }



    /** 발주 확정  */
    @PostMapping("/confirm/{poId}")
    public void confirmOrder(@PathVariable Long poId) {
        poService.confirmOrder(poId);
    }
}
