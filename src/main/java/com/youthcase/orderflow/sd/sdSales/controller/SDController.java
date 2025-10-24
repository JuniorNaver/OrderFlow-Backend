package com.youthcase.orderflow.sd.sdSales.controller;

import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.domain.SalesStatus;
import com.youthcase.orderflow.sd.sdSales.dto.AddItemRequest;
import com.youthcase.orderflow.sd.sdSales.dto.ConfirmOrderRequest;
import com.youthcase.orderflow.sd.sdSales.dto.SalesHeaderDTO;
import com.youthcase.orderflow.sd.sdSales.dto.SalesItemDTO;
import com.youthcase.orderflow.sd.sdSales.repository.SalesHeaderRepository;
import com.youthcase.orderflow.sd.sdSales.service.SDService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/sd")
@RequiredArgsConstructor
public class SDController {

    private final SDService sdService;
    private final SalesHeaderRepository salesHeaderRepository;

    // ✅ 1. 주문 생성
    @PostMapping("/create")
    public ResponseEntity<SalesHeaderDTO> createOrder(@RequestParam String storeId) {
        SalesHeader header = sdService.createOrder(storeId);
        SalesHeaderDTO dto = SalesHeaderDTO.from(header);
        return ResponseEntity.ok(dto);
    }

    // ✅ 2. 특정 주문 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<SalesHeaderDTO> getOrder(@PathVariable Long orderId) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        return ResponseEntity.ok(SalesHeaderDTO.from(header));
    }

    // ✅ 3. 상품 추가
    @PostMapping("/{orderId}/add-item")
    public ResponseEntity<SalesItemDTO> addItemToOrder(
            @PathVariable Long orderId,
            @RequestBody AddItemRequest request
    ) {
        request.setOrderId(orderId);
        SalesItemDTO dto = sdService.addItemToOrder(request);
        return ResponseEntity.ok(dto);
    }

    // ✅ 4. 주문별 아이템 조회
    @GetMapping("/{orderId}/items")
    public List<SalesItemDTO> getItemsByOrder(@PathVariable Long orderId) {
        return sdService.getItemsByOrderId(orderId);
    }

    // ✅ 5. 주문 확정 (결제 완료)
    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmOrder(@RequestBody ConfirmOrderRequest request) {
        sdService.confirmOrder(request);
        return ResponseEntity.ok().build();
    }

    // ✅ 6. 주문 완료 (결제 후 최종 완료)
    @PostMapping("/{orderId}/complete")
    public ResponseEntity<Void> completeOrder(@PathVariable Long orderId) {
        sdService.confirmOrder(new ConfirmOrderRequest(orderId, null, null)); // confirm 로직 재사용
        return ResponseEntity.ok().build();
    }

    // ✅ 7. 보류 저장
    @PostMapping("/{orderId}/hold")
    public ResponseEntity<Void> holdOrder(@PathVariable Long orderId) {
        sdService.holdOrder(orderId);
        return ResponseEntity.ok().build();
    }

    // ✅ 8. 보류 목록 조회
    @GetMapping("/holds")
    public List<SalesHeaderDTO> getHoldOrders() {
        return sdService.getHoldOrders();
    }

    // ✅ 9. 보류 재개
    @PostMapping("/{orderId}/resume")
    public ResponseEntity<SalesHeaderDTO> resumeOrder(@PathVariable Long orderId) {
        SalesHeaderDTO dto = sdService.resumeOrder(orderId);
        return ResponseEntity.ok(dto);
    }

    // ✅ 10. 보류 취소
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        sdService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    // ✅ 11. 보류 주문 내용 저장
    @PostMapping("/{orderId}/save")
    public ResponseEntity<Void> saveOrUpdateOrder(
            @PathVariable Long orderId,
            @RequestBody List<SalesItemDTO> items
    ) {
        sdService.saveOrUpdateOrder(orderId, items, SalesStatus.HOLD);
        return ResponseEntity.ok().build();
    }

    // ✅ 12. 수량 변경
    @PatchMapping("/items/{itemId}/quantity")
    public ResponseEntity<Void> updateItemQuantity(
            @PathVariable Long itemId,
            @RequestBody Map<String, Long> body
    ) {
        Long quantity = body.get("quantity");
        sdService.updateItemQuantity(itemId, quantity);
        return ResponseEntity.ok().build();
    }

    // ✅ 13. 에러 핸들링
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
