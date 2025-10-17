package com.youthcase.orderflow.master.service; // 별도의 impl 패키지를 사용하는 것이 일반적입니다.

import com.youthcase.orderflow.master.domain.Warehouse;
import com.youthcase.orderflow.master.dto.WarehouseUpdateDTO;
import com.youthcase.orderflow.master.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service // 빈 등록은 구현체에서 합니다.
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본은 읽기 전용으로 설정
public class WarehouseServiceImpl implements WarehouseService { // 인터페이스 구현

    private final WarehouseRepository warehouseRepository;

    // ====================================================================
    // 1. 창고 등록 (Create)
    // ====================================================================
    @Override
    @Transactional // 쓰기 작업에는 @Transactional 명시
    public Warehouse createWarehouse(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    // ====================================================================
    // 2. 전체 창고 목록 조회 (Read All)
    // ====================================================================
    @Override
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    // ====================================================================
    // 3. 특정 창고 ID로 상세 정보 조회 (Read One)
    // ====================================================================
    @Override
    public Warehouse getWarehouseById(String warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new NoSuchElementException("ID: " + warehouseId + "에 해당하는 창고를 찾을 수 없습니다."));
    }

    // ====================================================================
    // 4. 기존 창고 정보 수정 (Update)
    // ====================================================================
    @Override
    @Transactional
// DTO를 받도록 시그니처 변경!
    public Warehouse updateWarehouse(String warehouseId, WarehouseUpdateDTO updateDto) {
        Warehouse existingWarehouse = getWarehouseById(warehouseId); // 기존 Entity 조회

        // DTO의 데이터를 Entity에 반영
        existingWarehouse.setStorageCondition(updateDto.getStorageCondition());
        existingWarehouse.setMaxCapacity(updateDto.getMaxCapacity());
        existingWarehouse.setSpotId(updateDto.getSpotId());

        return warehouseRepository.save(existingWarehouse);
    }

    // ====================================================================
    // 5. 창고 정보 삭제 (Delete)
    // ====================================================================
    @Override
    @Transactional // 쓰기 작업에는 @Transactional 명시
    public void deleteWarehouse(String warehouseId) {
        warehouseRepository.deleteById(warehouseId);
    }
}