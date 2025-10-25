package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.domain.POStatus;
import com.youthcase.orderflow.po.dto.POHeaderResponseDTO;
import com.youthcase.orderflow.po.dto.POItemRequestDTO;
import com.youthcase.orderflow.po.dto.POItemResponseDTO;

import java.util.List;

public interface POService {

    /** POHeader 생성 */
    POHeaderResponseDTO createNewPOHeader();

    /** 기존 헤더에 아이템 추가 */
//    POItemResponseDTO addPOItem(POStatus status, POItemRequestDTO dto, String gtin);

    /** 모든 발주 헤더 조회 */
    List<POHeaderResponseDTO> findAll();

    /** 장바구니 내 모든 상품 조회 */
    List<POItemResponseDTO> getAllItems(Long poId);




    /** 상품 수량 변경 */
    POItemResponseDTO updateItemQuantity(Long itemNo, POItemRequestDTO requestDTO);

    /** 선택 상품 삭제 */
    void deleteItem(List<Long> itemNos);




    /** 장바구니 저장 */
    void saveCart(Long poId, String remarks);

    /** 저장된 장바구니 목록 조회 */
    List<POHeaderResponseDTO> getSavedCartList();

    /** 특정 장바구니 불러오기 */
    List<POItemResponseDTO> getSavedCartItems(Long poId);

    /** 저장된 장바구니 삭제 */
    void deletePO(Long poId);




    /** 발주 확정 처리 */
    void confirmOrder(Long poId);

    /** 입고 진행률 업데이트 */
    void updateReceiveProgress(Long poId);
}