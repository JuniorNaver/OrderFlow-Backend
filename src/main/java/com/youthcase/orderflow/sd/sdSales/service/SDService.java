package com.youthcase.orderflow.sd.sdSales.service;

import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import com.youthcase.orderflow.sd.sdSales.domain.SalesStatus;
import com.youthcase.orderflow.sd.sdSales.dto.AddItemRequest;
import com.youthcase.orderflow.sd.sdSales.dto.ConfirmOrderRequest;
import com.youthcase.orderflow.sd.sdSales.dto.SalesHeaderDTO;
import com.youthcase.orderflow.sd.sdSales.dto.SalesItemDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SDService{
    // 기본 판매로직
    // 주문(헤더) 생성
    SalesHeader createOrder();

    //상품 추가
    SalesItemDTO addItemToOrder(AddItemRequest request);

    // 바코드+재고
    void confirmOrder(ConfirmOrderRequest request);

    // 주문에 속한 아이템 목록 조회 보류도
    List<SalesItemDTO> getItemsByOrderId(Long orderId);


    // 주문 완료(결제 확정)
    void completeOrder(Long orderId);


    //보류 기능
    // 보류처리
    void holdOrder(Long orderId);


    // 보류 주문 취소(삭제)
    void cancelOrder(Long orderId);

    //보류 목록 불러오기
    List<SalesHeaderDTO> getHoldOrders();

    //보류된 주문 다시 열기
    public SalesHeaderDTO resumeOrder(Long orderId);

    void saveOrUpdateOrder(Long orderId, List<SalesItemDTO> items, SalesStatus status);

}