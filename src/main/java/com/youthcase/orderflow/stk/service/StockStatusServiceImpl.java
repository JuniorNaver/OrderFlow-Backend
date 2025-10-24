package com.youthcase.orderflow.stk.service;

import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.dto.ProgressStatusDTO;
import com.youthcase.orderflow.stk.dto.StockRelocationRequiredResponse;
import com.youthcase.orderflow.stk.repository.STKRepository;
import com.youthcase.orderflow.stk.mock.StockStatusMockData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        // ⭐️ Mock Data 사용
        return mockData.getMockCapacityStatus();
    }

    /**
     * 2. 유통기한 임박 현황 데이터를 Mock Data로 반환합니다.
     * @param days 임박 기준으로 삼을 일 수
     */
    @Override
    public ProgressStatusDTO getExpiryStatus(int days) {
        // ⭐️ Mock Data 사용
        return mockData.getMockExpiryStatus(days);
    }

    /**
     * 3. 특정 창고/지점의 FIFO 원칙을 위배하는 '위치 변경 필요' 재고 리스트를 조회합니다.
     * @param warehouseId 재고를 확인할 창고 또는 지점 ID
     * @return 위치 변경이 필요한 재고 DTO 리스트
     */
    @Override
    public List<StockRelocationRequiredResponse> getRelocationRequiredStocks(Long warehouseId) {

        // 1. 특정 창고의 활성 STK 리스트를 GTIN 및 유통기한 오름차순으로 정렬하여 조회
        List<STK> orderedStocks = stkRepository.findActiveStocksForFifoCheck(warehouseId);

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