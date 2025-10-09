package com.youthcase.orderflow.stk.service;

import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.dto.StockDeductionRequestDTO;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.util.List;

public interface STKService {

    /**
     * 전체 재고 목록 조회
     */
    List<STK> findAllStocks();

    /**
     * 재고 등록 (생성)
     */
    STK createStock(STK stock);

    /**
     * 단일 재고 조회
     */
    STK findStockById(Long stkId);

    /**
     * 재고 수량 및 상태 수정
     */
    STK updateStock(Long stkId, STK updatedStock);

    /**
     * 재고 삭제
     */
    void deleteStock(Long stkId);

    @Transactional
    void deductStockForSalesOrder(StockDeductionRequestDTO request);

    List<STK> getStockByProductGtin(String gtin);

    /**
     * 유통기한이 만료된 재고를 조회하고 상태를 폐기(DISPOSED)로 변경합니다.
     * @param targetDate 폐기 기준 날짜 (보통 new Date() 또는 Calendar를 이용한 오늘 자정)
     * @return 폐기 처리된 재고 목록
     */
    List<STK> disposeExpiredStock(Date targetDate);

    /**
     * 유통기한 임박 재고의 상태를 NEAR_EXPIRY로 변경하고 목록을 반환합니다.
     * @param targetDate 임박 재고를 판별할 기준 날짜 (보통 오늘)
     * @return 상태가 변경된 재고 목록
     */
    List<STK> markNearExpiryStock(java.util.Date targetDate);

}
