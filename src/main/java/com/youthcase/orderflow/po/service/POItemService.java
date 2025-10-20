package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.dto.POHeaderResponseDTO;
import com.youthcase.orderflow.po.dto.POItemRequestDTO;
import com.youthcase.orderflow.po.dto.POItemResponseDTO;
import com.youthcase.orderflow.po.domain.POStatus;

import java.util.List;

public interface POItemService {


    /** '담기' 클릭시 POItem 추가 */
    POItemResponseDTO addPOItem(Long poId, POItemRequestDTO poItemRequestDTO, String gtin);

    /** 장바구니 상품 조회 (poId, status 기준) */
    List<POItemResponseDTO> getAllItems(Long poId, POStatus status);

    /** 상품 수량 변경 */
    POItemResponseDTO updateItemQuantity(Long itemNo, POItemRequestDTO requestDTO);

    /** 선택 상품 삭제 */
    void deleteItem(List<Long> itemNos);



    /** 장바구니 저장 (헤더 ID와 함께 여러 아이템 저장) */
    void updateStatusToSaved(Long poId);

    /** 저장된 장바구니 목록 불러오기 (status = S 인 헤더 전체) */
    List<POHeaderResponseDTO> getSavedCartList(Long poId);

    /** 특정 장바구니 불러오기 */
    List<POItemResponseDTO> getSavedCartItems(Long poId);

}