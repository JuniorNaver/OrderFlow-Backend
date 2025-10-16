package com.youthcase.orderflow.stk.mock;

import com.youthcase.orderflow.stk.dto.ProgressStatusDTO;
import org.springframework.stereotype.Component;

/**
 * 재고 현황 관련 Mock 데이터를 제공하는 클래스.
 * 실제 DB 연동 전에 프론트엔드 개발 및 테스트를 위해 사용됩니다.
 */
@Component // Spring Bean으로 등록하여 Service에서 주입받아 사용할 수 있게 합니다.
public class StockStatusMockData {

    /**
     * 창고 적재 용량 현황 Mock 데이터 제공
     */
    public ProgressStatusDTO getMockCapacityStatus() {
        // Mock Data
        Long totalCapacity = 1000L;
        Long usedCapacity = 500L;

        return new ProgressStatusDTO(
                "창고 적재 용량 현황",
                totalCapacity,
                usedCapacity,
                "CBM"
        );
    }

    /**
     * 유통기한 임박 현황 Mock 데이터 제공
     * @param days 임박 기준으로 삼을 일 수
     */
    public ProgressStatusDTO getMockExpiryStatus(int days) {
        // Mock Data
        Long totalStock = 5000L;
        Long nearExpiryQuantity = 275L;

        return new ProgressStatusDTO(
                String.format("유통기한 임박 현황 (%d일 이내)", days),
                totalStock,
                nearExpiryQuantity,
                "개"
        );
    }
}