package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.dto.POItemRequestDTO;
import com.youthcase.orderflow.po.dto.POItemResponseDTO;
import com.youthcase.orderflow.po.domain.POStatus;

import java.util.List;

public interface POItemService {

    // 장바구니 상품 조회 (poId, status 기준)
    List<POItemResponseDTO> getAllItems(Long poId, POStatus status);

    // 상품 수량 변경
    POItemResponseDTO updateItemQuantity(Long itemNo, POItemRequestDTO requestDTO);

    // 선택 상품 삭제
    void deleteItem(List<Long> itemNos);

    // 장바구니 저장 (헤더 ID와 함께 여러 아이템 저장)
    void saveItem(Long poId, List<POItemRequestDTO> requestDTOList);
}