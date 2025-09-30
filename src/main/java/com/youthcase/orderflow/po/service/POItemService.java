package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.domain.POItem;

import java.util.List;

public interface POItemService {

    /**
     * 장바구니 전체 상품 조회
     */
    List<POItem> getAllItems();

    /**
     * 상품 수량 변경
     */
    POItem updateItemQuantity(String gtin, int quantity);

    /**
     * 선택 상품 삭제
     */
    void deleteItem(List<String> gtins);

    /**
     * 전체 상품 삭제
     */
    void clearCart();
}
