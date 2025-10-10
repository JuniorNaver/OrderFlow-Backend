package com.youthcase.orderflow.po.controller;

import com.youthcase.orderflow.po.domain.POStatus;
import com.youthcase.orderflow.po.dto.POItemRequestDTO;
import com.youthcase.orderflow.po.dto.POItemResponseDTO;
import com.youthcase.orderflow.po.service.POHeaderService;
import com.youthcase.orderflow.po.service.POItemService;
import com.youthcase.orderflow.po.service.POService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/po")
public class POController {

    private final POHeaderService poHeaderService;
    private final POItemService poItemService;
    private final POService poService;

    public POController(POHeaderService poHeaderService, POItemService poItemService, POService poService) {
        this.poHeaderService = poHeaderService;
        this.poItemService = poItemService;
        this.poService = poService;
    }

    /** 장바구니 상품 조회: 헤더id와 상태 기준으로 아이템 목록 찾아오기 */
    @GetMapping("/items")
    public List<POItemResponseDTO> getAllItems(Long poId, POStatus status) {
        return poItemService.getAllItems(poId, status);
    }

    /** 장바구니 상품 수량 변경 */
    @PutMapping("/items/{itemNo}")
    public POItemResponseDTO updateItemQuantity(@PathVariable Long itemNo, @RequestBody POItemRequestDTO dto) {
        return poItemService.updateItemQuantity(itemNo, dto);
    }

    /** 장바구니 상품 삭제  */
    @DeleteMapping("/items/delete")
    public void deleteItem(@RequestParam List<Long> itemIds) {
        poItemService.deleteItem(itemIds);
    }

    /** 장바구니 저장 */
    @PostMapping("/save/{poId}")
    public void saveItem(@PathVariable Long poId, List<POItemRequestDTO> requestDTOList){
        poItemService.saveItem(poId, requestDTOList);
    }

    /** 발주 확정  */
    @PostMapping("/confirm/{poId}")
    public void confirmOrder(@PathVariable Long poId) {
        poService.confirmOrder(poId);
    }
}
