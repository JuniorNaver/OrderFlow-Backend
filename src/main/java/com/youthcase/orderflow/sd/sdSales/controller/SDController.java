package com.youthcase.orderflow.sd.sdSales.controller;

import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import com.youthcase.orderflow.sd.sdSales.domain.SalesStatus;
import com.youthcase.orderflow.sd.sdSales.dto.AddItemRequest;
import com.youthcase.orderflow.sd.sdSales.dto.ConfirmOrderRequest;
import com.youthcase.orderflow.sd.sdSales.dto.SalesHeaderDTO;
import com.youthcase.orderflow.sd.sdSales.dto.SalesItemDTO;
import com.youthcase.orderflow.sd.sdSales.repository.SalesHeaderRepository;
import com.youthcase.orderflow.sd.sdSales.service.SDService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/sd")
@RequiredArgsConstructor
public class SDController {
    private final SDService sdService;
    private final SalesHeaderRepository salesHeaderRepository;
    // 1. 주문 생성 (판매등록 버튼 클릭시)
    @PostMapping("/create")
    public ResponseEntity<SalesHeaderDTO> createOrder(@RequestParam String storeId) {
        SalesHeader header = sdService.createOrder(storeId);
        SalesHeaderDTO dto = new SalesHeaderDTO(
                header.getOrderId(),
                header.getOrderNo(),
                header.getSalesDate(),
                header.getTotalAmount(),
                header.getSalesStatus()
        );
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<SalesHeaderDTO> getOrder(@PathVariable Long orderId) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        return ResponseEntity.ok(SalesHeaderDTO.from(header));
    }
    //상품 추가
    @PostMapping("/{orderId}/add-item")
    public ResponseEntity<SalesItemDTO> addItemToOrder(
            @PathVariable Long orderId,
            @RequestBody AddItemRequest request) {
        request.setOrderId(orderId);
        SalesItemDTO dto = sdService.addItemToOrder(request);
        return ResponseEntity.ok(dto);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    // 주문 확정
    @PostMapping("/confirm")
    public void confirmOrder(@RequestBody ConfirmOrderRequest request) {
        sdService.confirmOrder(request);
    }
    //3. 주문별 아이템 목록 조회
    @GetMapping("/{orderId}/items")
    public List<SalesItemDTO> getItems(@PathVariable Long orderId) {
        return sdService.getItemsByOrderId(orderId);
    }
    //4. 주문 완료(결제)
    @PostMapping("/{orderId}/complete")
    public void completeOrder(@PathVariable Long orderId) {
        sdService.completeOrder(orderId);
    }

    // ✅ 보류 시 전체 주문 정보 갱신
    @PostMapping("/{orderId}/hold")
        public void holdOrder(@PathVariable Long orderId, @RequestBody List<SalesItemDTO> items) {
            sdService.saveOrUpdateOrder(orderId, items, SalesStatus.HOLD);
        }

        //보류 목록 조회
        @GetMapping("/hold")
        public List<SalesHeaderDTO> getHoldOrders() {
            return sdService.getHoldOrders();
        }
        //보류 주문 다시 열기
        @PostMapping("/{orderId}/resume")
        public SalesHeaderDTO resumeOrder(@PathVariable Long orderId) {
            return sdService.resumeOrder(orderId);
        }
        //보류 취소
        @DeleteMapping("/{orderId}/cancel")
        public void cancelOrder(@PathVariable Long orderId) {
            sdService.cancelOrder(orderId);
        }

        @PostMapping("/{orderId}/save")
        public ResponseEntity<Void> saveOrUpdateOrder(
                @PathVariable Long orderId,
                @RequestBody List<SalesItemDTO> items
    ) {
            sdService.saveOrUpdateOrder(orderId, items, SalesStatus.HOLD);
            return ResponseEntity.ok().build();
        }

    }