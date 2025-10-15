package com.youthcase.orderflow.stk.service;

import com.youthcase.orderflow.stk.dto.ProgressStatusDTO;
import com.youthcase.orderflow.stk.repository.STKRepository; // 실제 DB 연동 시 필요
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 데이터 조회 성능을 위해 readOnly 트랜잭션 적용

// @Service 어노테이션을 사용하여 Spring Bean으로 등록합니다.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 현황 조회는 읽기 전용이므로 성능 최적화를 위해 설정
public class StockStatusService {

    // 현재는 Mock Data를 사용하지만, 최종적으로는 STKRepository를 사용해야 합니다.
    private final STKRepository stkRepository;

    /**
     * 1. 창고 적재 용량 현황 데이터를 Mock Data로 계산하여 반환합니다.
     * @return ProgressStatusDTO (사용 용량/총 용량)
     */
    public ProgressStatusDTO getCapacityStatus() {

        // ⭐️ Mock Data
        Long totalCapacity = 1000L; // Mock Total
        Long usedCapacity = 780L;   // Mock Current (사용 중)

        // [TODO: 실제 구현 시]
        // Long usedCapacity = stkRepository.sumActiveQuantity();
        // Long totalCapacity = warehouseRepository.getTotalCapacity();

        return new ProgressStatusDTO(
                "창고 적재 용량 현황",
                totalCapacity,
                usedCapacity,
                "CBM" // 단위는 실제 창고 시스템에 맞게 조정
        );
    }

    /**
     * 2. 유통기한 임박 현황 데이터를 Mock Data로 계산하여 반환합니다.
     * @param days 임박 기준으로 삼을 일 수 (React에서 쿼리 파라미터로 받음)
     * @return ProgressStatusDTO (임박 수량/전체 수량)
     */
    public ProgressStatusDTO getExpiryStatus(int days) {

        // ⭐️ Mock Data
        Long totalStock = 5000L; // Mock Total
        Long nearExpiryQuantity = 1275L; // Mock Current (위험 수량)

        // [TODO: 실제 구현 시]
        // LocalDate expiryLimitDate = LocalDate.now().plusDays(days);
        // Long totalStock = stkRepository.sumActiveQuantity();
        // List<STK> nearExpiryStocks = stkRepository.findNearExpiryActiveStock(expiryLimitDate);
        // Long nearExpiryQuantity = nearExpiryStocks.stream().mapToLong(STK::getQuantity).sum();

        return new ProgressStatusDTO(
                String.format("유통기한 임박 현황 (%d일 이내)", days),
                totalStock,
                nearExpiryQuantity,
                "개" // 단위는 실제 상품 수량에 맞게 조정
        );
    }
}