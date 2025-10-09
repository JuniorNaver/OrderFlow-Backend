package com.youthcase.orderflow.stk.service;

import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.dto.StockDeductionRequestDTO;
import org.springframework.transaction.annotation.Transactional;

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
        // 쓰기 트랜잭션으로 명시
    void deductStockForSalesOrder(StockDeductionRequestDTO request);
}