package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.dto.*;
import java.util.List;

/**
 * ⚙️ POService
 * - Controller와 Repository 사이의 비즈니스 로직 인터페이스
 * - 트랜잭션 단위의 서비스 계층 책임 분리
 */
public interface POService {

    /** 장바구니(PR) 생성 또는 기존 PR에 상품 추가 */
    POItemResponseDTO addOrCreatePOItem(String userId, POItemRequestDTO dto);

    /** 전체 발주 헤더 조회 (관리자용) */
    List<POHeaderResponseDTO> findAll();

    /** 특정 장바구니 내 상품 목록 조회 */
    List<POItemResponseDTO> getAllItems(Long poId);



    /** 상품 수량 변경 */
    POItemResponseDTO updateItemQuantity(Long itemNo, POItemRequestDTO requestDTO);

    /** 선택 상품 삭제 */
    void deleteItem(List<Long> itemNos);



    /** 장바구니 저장 (status=S 인 헤더, 아이템 한 행 복제) */
    void saveCart(Long poId, String remarks);

    /** 저장된 장바구니 목록 조회 */
    List<POHeaderResponseDTO> getSavedCartList();

    /** 특정 저장 장바구니 불러오기 */
    List<POItemResponseDTO> getSavedCartItems(Long poId);

    /** 불러오기 버튼을 누르면 status=PR인 헤더, 아이템 한 행 복제 */
    Long loadCart(Long savedPoId);

    /** 저장된 장바구니 삭제 */
    void deletePO(Long poId);



    /** 발주 확정 (S → PO) */
    void confirmOrder(Long poId);

    /** 입고 진행률 업데이트 (GI → FULLY_RECEIVED) */
    void updateReceiveProgress(Long poId);

    /** 최신 PR 상태 헤더 조회 (없으면 null) */
    Long getCurrentCartId();
}
