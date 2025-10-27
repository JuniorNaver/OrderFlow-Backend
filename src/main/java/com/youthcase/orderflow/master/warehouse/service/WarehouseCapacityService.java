package com.youthcase.orderflow.master.warehouse.service;

import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import com.youthcase.orderflow.master.warehouse.repository.WarehouseRepository;
import com.youthcase.orderflow.stk.domain.StockStatus;
import com.youthcase.orderflow.stk.repository.STKRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 📦 창고 적재 용량(CBM) 자동 계산 서비스
 * - 재고(STK) 테이블 기준으로 currentCapacity 값을 최신화
 * - STKService, WarehouseService 모두에서 공통 사용
 */
@Service
@RequiredArgsConstructor
public class WarehouseCapacityService {

    private final STKRepository stkRepository;
    private final WarehouseRepository warehouseRepository;

    // 적재량 합산에 포함되는 재고 상태
    public static final List<StockStatus> STOCKED_STATUSES = List.of(
            StockStatus.ACTIVE, StockStatus.NEAR_EXPIRY, StockStatus.EXPIRED, StockStatus.RETURNED
    );

    /**
     * 🧮 단일 창고 기준 적재 용량(CBM) 재계산 및 갱신
     */
    @Transactional
    public void updateWarehouseCapacity(String warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new NoSuchElementException("창고를 찾을 수 없습니다: " + warehouseId));

        List<Object[]> result = stkRepository.sumCbmByWarehouse(STOCKED_STATUSES);
        double totalCbm = result.stream()
                .filter(r -> warehouseId.equals(r[0]))
                .mapToDouble(r -> ((Number) r[1]).doubleValue())
                .findFirst()
                .orElse(0.0);

        if (!cbmEquals(warehouse.getCurrentCapacity(), totalCbm)) {
            warehouse.setCurrentCapacity(totalCbm);
            warehouseRepository.save(warehouse);
        }
    }

    /**
     * 🧩 전체 창고(혹은 특정 지점) 일괄 재계산
     */
    @Transactional
    public void updateAllWarehouseCapacities() {
        List<Object[]> result = stkRepository.sumCbmByWarehouse(STOCKED_STATUSES);
        Map<String, Double> cbmMap = result.stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> ((Number) r[1]).doubleValue()
                ));

        List<Warehouse> warehouses = warehouseRepository.findAll();
        warehouses.forEach(w -> {
            double cbm = cbmMap.getOrDefault(w.getWarehouseId(), 0.0);
            if (!cbmEquals(w.getCurrentCapacity(), cbm)) {
                w.setCurrentCapacity(cbm);
                warehouseRepository.save(w);
            }
        });
    }

    private boolean cbmEquals(Double a, Double b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return Math.abs(a - b) < 0.0001;
    }
}
