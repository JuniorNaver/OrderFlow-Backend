package com.youthcase.orderflow.master.warehouse.service;

import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import com.youthcase.orderflow.master.warehouse.dto.WarehouseRequestDTO;
import com.youthcase.orderflow.master.store.repository.StoreRepository;
import com.youthcase.orderflow.master.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final StoreRepository storeRepository;
    private final WarehouseCapacityService capacityService;

    // ────────────────────────────────
    // 🔹 1. 창고 등록
    // ────────────────────────────────
    @Transactional
    public Warehouse createWarehouse(WarehouseRequestDTO dto) {
        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 지점 ID입니다: " + dto.getStoreId()));

        Warehouse warehouse = dto.toEntity(store);
        return warehouseRepository.save(warehouse);
    }

    // ────────────────────────────────
    // 🔹 2. 전체 조회
    // ────────────────────────────────
    public List<Warehouse> getAllWarehouses() {
        capacityService.updateAllWarehouseCapacities();
        return warehouseRepository.findAll();
    }

    // ────────────────────────────────
    // 🔹 3. 단일 조회
    // ────────────────────────────────
    public Warehouse getWarehouseById(String warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new NoSuchElementException("창고를 찾을 수 없습니다: " + warehouseId));
    }

    // ────────────────────────────────
    // 🔹 4. 창고 정보 수정
    // ────────────────────────────────
    @Transactional
    public Warehouse updateWarehouse(String warehouseId, WarehouseRequestDTO dto) {
        Warehouse warehouse = getWarehouseById(warehouseId);

        // ✅ Store 재조회 (지점 변경 시)
        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 지점 ID입니다: " + dto.getStoreId()));

        // ✅ DTO의 변경사항 적용
        dto.applyToEntity(warehouse, store);

        return warehouseRepository.saveAndFlush(warehouse);
    }

    // ────────────────────────────────
    // 🔹 5. 창고 삭제
    // ────────────────────────────────
    @Transactional
    public void deleteWarehouse(String warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new NoSuchElementException("삭제할 창고를 찾을 수 없습니다: " + warehouseId);
        }
        warehouseRepository.deleteById(warehouseId);
    }

    // ────────────────────────────────
    // 🔹 6. 점포별 창고 조회
    // ────────────────────────────────
    public List<Warehouse> getWarehousesByStoreId(String storeId) {
        capacityService.updateAllWarehouseCapacities();
        return warehouseRepository.findByStore_StoreId(storeId);
    }
}