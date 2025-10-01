package com.youthcase.orderflow.po.controller;

import com.youthcase.orderflow.po.domain.PO;
import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.service.POHeaderService;
import com.youthcase.orderflow.po.service.POItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/po")
public class POController {

    private final POHeaderService poHeaderService;
    private final POItemService poItemService;

    public POController(POHeaderService poHeaderService, POItemService poItemService) {
        this.poHeaderService = poHeaderService;
        this.poItemService = poItemService;
    }

    // ========================
    // 발주 헤더(PO) 관리
    // ========================

    /** 발주 전체 목록 조회 */
    @GetMapping
    public List<PO> getAllPOs() {
        return poHeaderService.findAll();
    }

    /** 발주 생성 (단독 생성이 필요한 경우) */
    @PostMapping
    public PO createPO(@RequestBody PO po) {
        return poHeaderService.save(po);
    }

    // ========================
    // 장바구니(POItem) 관리
    // ========================

    /** 장바구니 목록 조회 (status = PR 인 아이템만 가져오기) */
    @GetMapping("/items")
    public List<POItem> getCartItems() {
        return poItemService.findCartItems();  // status = PR
    }

    /** 장바구니에 아이템 추가 */
    @PostMapping("/items")
    public POItem addItem(@RequestBody POItem item) {
        return poItemService.addItemToCart(item);  // status 기본값 = PR
    }

    /** 장바구니 아이템 수량 변경 */
    @PutMapping("/items/{itemId}")
    public POItem updateItemQuantity(@PathVariable Long itemId,
                                     @RequestBody POItem item) {
        return poItemService.updateQuantity(itemId, item.getQuantity());
    }

    /** 장바구니 아이템 삭제 (status = D 로 처리) */
    @DeleteMapping("/items/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        poItemService.markAsDeleted(itemId);  // status = D
    }

    /** 장바구니 아이템 여러 건 삭제 */
    @DeleteMapping("/items")
    public void deleteItems(@RequestBody List<Long> itemIds) {
        poItemService.markAsDeleted(itemIds); // 일괄 status = D
    }

    // ========================
    // 발주 확정 (장바구니 → 발주)
    // ========================

    /** 장바구니 확정 → POHeader 생성 + 아이템 status = PO */
    @PostMapping("/checkout")
    public PO checkout() {
        return poHeaderService.checkout();
        // 내부에서:
        // 1) POHeader 생성
        // 2) Cart(PR) 상태 아이템 전부 status = PO 로 업데이트
        // 3) headerId 연결
    }
}
