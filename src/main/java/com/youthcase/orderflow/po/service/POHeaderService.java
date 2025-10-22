package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POStatus;
import com.youthcase.orderflow.po.dto.POHeaderResponseDTO;
import com.youthcase.orderflow.po.dto.POItemRequestDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface POHeaderService {

    /** PO생성 */
    Long createNewPO();

    /** 모든 발주 헤더 조회 */
    List<POHeaderResponseDTO> findAll();

    /** POHeader, POItem 생성 */
    Long createHeaderAndAddItem(String gtin, POItemRequestDTO dto);

    /** 장바구니 저장*/
    void saveCart(Long poId, String remarks);

    /** 저장된 장바구니 불러오기 */
    List<POHeaderResponseDTO> getSavedCartList();

    /** 저장한 장바구니 삭제 */
    void deletePO(Long poId);



}

