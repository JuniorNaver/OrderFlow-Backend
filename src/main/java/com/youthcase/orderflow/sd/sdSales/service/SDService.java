package com.youthcase.orderflow.sd.sdSales.service;

import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.domain.SalesStatus;
import com.youthcase.orderflow.sd.sdSales.dto.AddItemRequest;
import com.youthcase.orderflow.sd.sdSales.dto.ConfirmOrderRequest;
import com.youthcase.orderflow.sd.sdSales.dto.SalesHeaderDTO;
import com.youthcase.orderflow.sd.sdSales.dto.SalesItemDTO;

import java.util.List;

public interface SDService {

    // ✅ 주문 생성
    SalesHeader createOrder(String storeId);

    // ✅ 상품 추가
    SalesItemDTO addItemToOrder(AddItemRequest request);

    // 상품삭제
    SalesHeaderDTO deleteItemFromOrder(Long orderId, Long itemId);

    // ✅ 주문별 아이템 조회
    List<SalesItemDTO> getItemsByOrderId(Long orderId);

    // ✅ 주문 확정 (결제 완료 시)
    void confirmOrder(ConfirmOrderRequest request);

    // ✅ 주문 보류 처리
    void holdOrder(Long orderId);

    // ✅ 보류 주문 재개
    SalesHeaderDTO resumeOrder(Long orderId);

    // ✅ 보류 주문 저장 (업데이트)
    void saveOrUpdateOrder(Long orderId, List<SalesItemDTO> items, SalesStatus status);

    // ✅ 보류 취소 (재고 복원)
    void cancelOrder(Long orderId);

    List<SalesHeaderDTO> getHoldOrders();

    void updateItemQuantity(Long itemId, Long quantity);
}
