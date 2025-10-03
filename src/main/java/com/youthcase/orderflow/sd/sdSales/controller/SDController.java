package com.youthcase.orderflow.sd.sdSales.controller;

import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.dto.ConfirmOrderRequest;
import com.youthcase.orderflow.sd.sdSales.dto.SalesHeaderDTO;
import com.youthcase.orderflow.sd.sdSales.dto.SalesItemDTO;
import com.youthcase.orderflow.sd.sdSales.service.SDService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/sd")
@RequiredArgsConstructor
public class SDController {

    private final SDService sdService;

    // 1. 주문 생성 (판매등록 버튼 클릭시)
    @PostMapping("/create")
    public SalesHeader createOrder() {
        return sdService.createOrder();
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

    //보류처리
    @PostMapping("/{orderId}/hold")
    public void holdOrder(@PathVariable Long orderId) {
        sdService.holdOrder(orderId);
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

}
