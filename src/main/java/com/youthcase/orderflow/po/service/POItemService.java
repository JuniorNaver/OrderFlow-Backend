package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.domain.Status;

import java.util.List;

public interface POItemService {

    // 장바구니 상품 조회: 헤더id와 상태 기준으로 아이템 목록 찾아오기
    List<POItem> getAllItems(Long poId, Status status);

    // 상품 수량 변경
    POItem updateItemQuantity(Long itemId, Long quantity);

    // 선택 상품 삭제
    void deleteItem(List<Long> itemIds);

    // 장바구니 저장
    void saveItem(Long poId);
}
