package com.youthcase.orderflow.stk.service;

import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.dto.ProgressStatusDTO; // 👈 DTO import 추가
import com.youthcase.orderflow.stk.dto.StockDeductionRequestDTO;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate; // 👈 날짜 타입을 LocalDate로 통일
import java.util.Date;
import java.util.List;

public interface STKService {

    // ... (기존 CRUD 및 차감 메서드 생략)

    List<STK> findAllStocks();
    STK createStock(STK stock);
    STK findStockById(Long stkId);
    STK updateStock(Long stkId, STK updatedStock);
    void deleteStock(Long stkId);
    @Transactional
    void deductStockForSalesOrder(StockDeductionRequestDTO request);
    List<STK> getStockByProductGtin(String gtin);

    // --------------------------------------------------
    // 📊 대시보드 현황 조회 메서드 추가
    // --------------------------------------------------

    /**
     * 창고 적재 용량 현황 데이터를 조회합니다.
     * @return ProgressStatusDTO (사용 용량/총 용량)
     */
    ProgressStatusDTO getCapacityStatus();

    /**
     * 유통기한 임박 현황 데이터를 조회합니다.
     * @param days 임박 기준으로 삼을 일 수 (예: 90일)
     * @return ProgressStatusDTO (임박 수량/전체 수량)
     */
    ProgressStatusDTO getExpiryStatus(int days);


    // --------------------------------------------------
    // 유통기한 처리 로직 수정 (LocalDate로 변경)
    // --------------------------------------------------

    // Date 대신 LocalDate를 사용하여 일관성 및 안정성 확보
    List<STK> disposeExpiredStock(LocalDate targetDate);
    List<STK> markNearExpiryStock(LocalDate targetDate);

}