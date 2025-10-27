package com.youthcase.orderflow.stk.service;

import com.youthcase.orderflow.master.warehouse.service.WarehouseCapacityService;
import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.domain.StockStatus;
import com.youthcase.orderflow.stk.dto.ProgressStatusDTO;
import com.youthcase.orderflow.stk.dto.StockRelocationRequiredResponse;
import com.youthcase.orderflow.stk.repository.STKRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 📊 StockStatusServiceImpl
 * - 창고 적재 현황, 유통기한 임박 재고, FIFO 위배 재고 조회
 * - MockData 제거 → 실제 DB 기반으로 연산
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockStatusServiceImpl implements StockStatusService {

    // 실제 DB 연동 시 필요
    private final STKRepository stkRepository;
    // ✅ 창고 용량 자동 갱신용
    private final WarehouseCapacityService capacityService;

    private static final List<StockStatus> STOCKED_STATUSES = WarehouseCapacityService.STOCKED_STATUSES;


    /**
     * ✅ 1. 창고 적재 용량 현황 조회
     * - STK 기반으로 각 창고별 CBM 총합을 계산하고,
     * - WarehouseCapacityService를 통해 최신화된 상태로 반환합니다.
     */
    @Override
    public ProgressStatusDTO getCapacityStatus() {
        // 창고 전체 용량 최신화 (STK 반영)
        capacityService.updateAllWarehouseCapacities();

        // STK에서 실제 CBM 합산 데이터 조회
        List<Object[]> cbmList = stkRepository.sumCbmByWarehouse(STOCKED_STATUSES);

        double totalCapacity = cbmList.stream()
                .mapToDouble(r -> ((Number) r[1]).doubleValue())
                .sum();

        // 전체 용량 대비 사용량 비율을 계산 (MockData 대체)
        double usedCapacity = totalCapacity; // 현 시스템에선 창고 전체 합산 = 사용량
        double maxCapacity = Math.max(usedCapacity * 1.3, 1.0); // 총용량 추정값 (예: 여유분 30%)

        return new ProgressStatusDTO(
                "창고 적재 용량 현황",
                (long) maxCapacity,
                (long) usedCapacity,
                "CBM"
        );
    }

    /**
     * ✅ 2. 유통기한 임박 재고 현황 조회
     * - 현재일 기준 `days`일 이내에 만료되는 재고를 합산합니다.
     */
    @Override
    public ProgressStatusDTO getExpiryStatus(int days) {
        LocalDate limitDate = LocalDate.now().plusDays(days);

        // 유통기한 임박 재고 조회 (NEAR_EXPIRY 조회)
        List<STK> nearExpiryStocks =
                stkRepository.findNearExpiryActiveStock(limitDate, StockStatus.NEAR_EXPIRY);

        long currentQuantity = nearExpiryStocks.stream()
                .mapToLong(STK::getQuantity)
                .sum();

        long totalQuantity = Optional.ofNullable(
                stkRepository.sumActiveQuantity(STOCKED_STATUSES)
        ).orElse(0L);

        return new ProgressStatusDTO(
                "유통기한 임박 현황",
                totalQuantity,
                currentQuantity,
                "개"
        );
    }

    /**
     * 3. 특정 창고/지점의 FIFO 원칙을 위배하는 '위치 변경 필요' 재고 리스트를 조회합니다.
     * @param warehouseId 재고를 확인할 창고 또는 지점 ID
     * @return 위치 변경이 필요한 재고 DTO 리스트
     */
    @Override
    public List<StockRelocationRequiredResponse> getRelocationRequiredStocks(String warehouseId) {

        // 1. 특정 창고의 활성 STK 리스트를 GTIN 및 유통기한 오름차순으로 정렬하여 조회
        StockStatus targetStatus = StockStatus.ACTIVE;
        List<STK> orderedStocks = stkRepository.findActiveStocksForFifoCheck(warehouseId, targetStatus);

        // 2. 제품 ID(GTIN)별로 그룹화
        Map<String, List<STK>> groupedStocks = orderedStocks.stream()
                .collect(Collectors.groupingBy(s -> s.getProduct().getGtin(),
                        // 혹시 모를 경우를 대비해 다시 유통기한 순으로 정렬을 보장합니다.
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    list.sort(Comparator.comparing(s -> s.getLot().getExpDate()));
                                    return list;
                                }
                        )
                ));

        List<StockRelocationRequiredResponse> requiredList = new ArrayList<>();

        // 3. FIFO 위배 검사 로직 (핵심)
        for (List<STK> stocks : groupedStocks.values()) {
            for (int i = 0; i < stocks.size() - 1; i++) {
                STK currentSTK = stocks.get(i);
                Long currentQty = currentSTK.getQuantity();

                if (currentQty == null || currentQty <= 0) continue;

                // ⭐️ isAlreadyAdded 변수는 이제 필요하지 않습니다.
                // boolean isAlreadyAdded = false;

                for (int j = i + 1; j < stocks.size(); j++) {
                    STK laterSTK = stocks.get(j);
                    Long laterQty = laterSTK.getQuantity();

                    if (laterQty == null || laterQty <= 0) continue;

                    // [FIFO 위배 조건]
                    if (laterQty > currentQty) {

                        // ⭐️ 조건문 제거 및 DTO 추가 로직 통합
                        requiredList.add(new StockRelocationRequiredResponse(
                                currentSTK.getLot().getLotId(),
                                currentSTK.getStkId(),
                                currentSTK.getProduct().getGtin(),
                                currentSTK.getProduct().getProductName(),
                                currentSTK.getWarehouse().getWarehouseId(),
                                currentSTK.getLot().getExpDate(),
                                currentQty,
                                "FIFO 위배: 후입 재고 수량 많음"
                        ));
                        // isAlreadyAdded = true; // 제거

                        // ⭐️ 위배가 발견되면 이 랏(currentSTK)은 위치 변경 대상으로 확정되므로
                        //    더 이상 검사할 필요 없이 내부 루프를 종료합니다.
                        break;
                    }
                }
            }
        }
        return requiredList;
    }
}