package com.youthcase.orderflow.sd.sdSales.service;

import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;

import java.math.BigDecimal;
import java.util.List;

public interface SDService{
    // 기본 판매로직
    // 주문(헤더) 생성
    SalesHeader createOrder();

    // 바코드로 아이템 추가
    void addItemByBarcode(Long orderId, String gtin, int quantity, BigDecimal sdPrice);

    // 주문에 속한 아이템 목록 조회
    List<SalesItem> getItemsByOrderId(Long orderId);


    // 주문 완료(결제 확정)
    void completeOrder(Long orderId);

    // 재고 수정
    void updateItemQuantity(Long orderId, String gtin, int diff);

    //보류 기능
    // 보류처리
    void holdOrder(Long orderId);

    // 보류 주문 취소(삭제)
    void cancelOrder(Long orderId);

    //보류 목록 불러오기
    List<SalesHeader> getHoldOrders();

    //보류된 주문 다시 열기
    void resumeOrder(Long orderId);

}