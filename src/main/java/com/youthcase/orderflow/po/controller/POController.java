package com.youthcase.orderflow.po.controller;

import com.youthcase.orderflow.po.domain.POStatus;
import com.youthcase.orderflow.po.dto.POHeaderResponseDTO;
import com.youthcase.orderflow.po.dto.POItemRequestDTO;
import com.youthcase.orderflow.po.dto.POItemResponseDTO;
import com.youthcase.orderflow.po.service.POHeaderService;
import com.youthcase.orderflow.po.service.POItemService;
import com.youthcase.orderflow.po.service.POService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class POController {

    private final POHeaderService poHeaderService;
    private final POItemService poItemService;
    private final POService poService;

    public POController(POHeaderService poHeaderService, POItemService poItemService, POService poService) {
        this.poHeaderService = poHeaderService;
        this.poItemService = poItemService;
        this.poService = poService;
    }


    /** '담기' 클릭시 POHeader + POItem 함께 생성 */
    @PostMapping("/po") // POST /api/po
    public ResponseEntity<Map<String, Long>> createPO() {
        Long poId = poHeaderService.createNewPO(); // status = PR
        return ResponseEntity.ok(Map.of("poId", poId));
    }
    @PostMapping("/po/{poId}/items")
    public ResponseEntity<POItemResponseDTO> addPOItem(
            @PathVariable Long poId,
            @RequestBody POItemRequestDTO poItemRequestDTO,
            @RequestParam String gtin
    ) {
        POItemResponseDTO response = poItemService.addPOItem(poId, poItemRequestDTO, gtin);
        return ResponseEntity.ok(response);
    }





    /** '장바구니로 가기' 눌렀을 때 장바구니 조회 */
    @GetMapping("/po/items")
    public List<POItemResponseDTO> getAllItems(@RequestParam Long poId) {
        return poItemService.getAllItems(poId);
    }




    /** 상품 수량 변경 */
    @PutMapping("/po/update/{itemNo}")
    public POItemResponseDTO updateItemQuantity(@PathVariable Long itemNo, @RequestBody POItemRequestDTO dto) {
        return poItemService.updateItemQuantity(itemNo, dto);
    }
    /** 상품 삭제 */
    @DeleteMapping("/po/delete")
    public void deleteItem(@RequestParam List<Long> itemIds) {
        poItemService.deleteItem(itemIds);
    }





    /** 장바구니 저장 */
    @PostMapping("/po/save/{poId}")
    public ResponseEntity<String> saveCart(
            @PathVariable Long poId,
            @RequestBody Map<String, String> requestBody
    ) {
        String remarks = requestBody.get("remarks");
        poHeaderService.saveCart(poId, remarks);
        return ResponseEntity.ok().build();
    }
    /** 장바구니 목록 불러오기 */
    @GetMapping("/po/saved")
    public ResponseEntity<List<POHeaderResponseDTO>> getSavedCartList() {
        List<POHeaderResponseDTO> savedList = poHeaderService.getSavedCartList();
        return ResponseEntity.ok(savedList);
    }
    /** 특정 장바구니 불러오기 */
    @GetMapping("/po/savedCart/{poId}")
    public ResponseEntity<List<POItemResponseDTO>> getSavedCart(@PathVariable Long poId) {
        List<POItemResponseDTO> savedItems = poItemService.getSavedCartItems(poId);
        return ResponseEntity.ok(savedItems);
    }
    /** 저장한 장바구니 삭제 */
    @DeleteMapping("/po/delete/{poId}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long poId) {
        poHeaderService.deletePO(poId);
        return ResponseEntity.noContent().build(); // 204 반환
    }



    //
    /** 발주 확정  */
    //@PreAuthorize("hasAuthority('PO_CONFIRM')")
    @PostMapping("/po/confirm/{poId}")
    public void confirmOrder(@PathVariable Long poId) {
        poService.confirmOrder(poId);
    }
}
