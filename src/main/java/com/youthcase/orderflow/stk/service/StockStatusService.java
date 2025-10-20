package com.youthcase.orderflow.stk.service;

import com.youthcase.orderflow.stk.dto.ProgressStatusDTO;
import com.youthcase.orderflow.stk.dto.StockRelocationRequiredResponse; // ⭐️ 추가: 반환 타입을 위해 import

import java.util.List;

/**
 * 재고 현황 조회 서비스를 위한 인터페이스입니다.
 */
public interface StockStatusService {

    /**
     * 창고 적재 용량 현황 데이터를 반환합니다.
     * @return ProgressStatusDTO (사용 용량/총 용량)
     */
    ProgressStatusDTO getCapacityStatus();

    /**
     * 유통기한 임박 현황 데이터를 반환합니다.
     * @param days 임박 기준으로 삼을 일 수
     * @return ProgressStatusDTO (임박 수량/전체 수량)
     */
    ProgressStatusDTO getExpiryStatus(int days);

    /**
     * ⭐️ 추가: FIFO 위배로 인해 위치 변경이 필요한 재고 리스트를 반환합니다.
     * @param warehouseId 재고를 확인할 창고 또는 지점 ID
     * @return 위치 변경이 필요한 재고 리스트
     */
    List<StockRelocationRequiredResponse> getRelocationRequiredStocks(Long warehouseId);
}