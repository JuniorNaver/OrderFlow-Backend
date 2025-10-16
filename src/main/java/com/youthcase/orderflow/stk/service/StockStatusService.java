package com.youthcase.orderflow.stk.service;

import com.youthcase.orderflow.stk.dto.ProgressStatusDTO;

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
}
