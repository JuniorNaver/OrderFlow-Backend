package com.youthcase.orderflow.stk.service;

import com.youthcase.orderflow.stk.dto.ProgressStatusDTO;
import com.youthcase.orderflow.stk.repository.STKRepository; // 실제 DB 연동 시 필요
import com.youthcase.orderflow.stk.mock.StockStatusMockData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// @Service 어노테이션을 사용하여 Spring Bean으로 등록하고 인터페이스를 구현합니다.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockStatusServiceImpl implements StockStatusService {

    // 실제 DB 연동 시 필요
    private final STKRepository stkRepository;
    // Mock Data 주입
    private final StockStatusMockData mockData;

    /**
     * 1. 창고 적재 용량 현황 데이터를 Mock Data로 반환합니다.
     */
    @Override
    public ProgressStatusDTO getCapacityStatus() {

        // ⭐️ Mock Data 사용 (운영 환경에서는 if문으로 Mock 사용 여부 분기 처리 권장)
        return mockData.getMockCapacityStatus();

        /*
        // [TODO: 실제 구현 시]
        // Long usedCapacity = stkRepository.sumActiveQuantity();
        // Long totalCapacity = warehouseRepository.getTotalCapacity();
        // return new ProgressStatusDTO( ... );
        */
    }

    /**
     * 2. 유통기한 임박 현황 데이터를 Mock Data로 반환합니다.
     * @param days 임박 기준으로 삼을 일 수
     */
    @Override
    public ProgressStatusDTO getExpiryStatus(int days) {

        // ⭐️ Mock Data 사용 (운영 환경에서는 if문으로 Mock 사용 여부 분기 처리 권장)
        return mockData.getMockExpiryStatus(days);

        /*
        // [TODO: 실제 구현 시]
        // LocalDate expiryLimitDate = LocalDate.now().plusDays(days);
        // List<STK> nearExpiryStocks = stkRepository.findNearExpiryActiveStock(expiryLimitDate);
        // ... (실제 DB 조회 로직)
        */
    }
}
