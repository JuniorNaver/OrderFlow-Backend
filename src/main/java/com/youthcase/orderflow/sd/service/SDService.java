package com.youthcase.orderflow.sd.service;

import com.youthcase.orderflow.sd.domain.SalesItem;

import java.util.List;

public interface SDService{
    List<SalesItem> salesItemList(SalesItem salesItem);
    void salesItemInsert(SalesItem salesItem);

    SalesItem getSalesItem(Long itemId);                  // 단일 조회
    SalesItem updateSalesItem(SalesItem salesItem);       // 수정
    void deleteSalesItem(Long itemId);                    // 삭제
    List<SalesItem> getItemsByOrderId(Long orderId);      // 주문별 조회
    void addItemByBarcode(Long orderId, String gtin, int quantity); // 바코드 기반 추가

}