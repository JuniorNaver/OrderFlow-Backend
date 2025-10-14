package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POStatus;
import com.youthcase.orderflow.po.dto.POHeaderResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface POHeaderService {

    /** 모든 발주 헤더 조회 */
    List<POHeaderResponseDTO> findAll();

    /** 장바구니 저장*/
    void updateStatusToSaved(Long poId);

    /** 상태로 찾기 */
    public interface POHeaderRepository extends JpaRepository<POHeader, Long> {
        List<POHeader> findByStatus(POStatus status);
    }
}