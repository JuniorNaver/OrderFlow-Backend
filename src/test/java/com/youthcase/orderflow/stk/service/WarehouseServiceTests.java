package com.youthcase.orderflow.stk.service;

import com.youthcase.orderflow.master.domain.Warehouse;
import com.youthcase.orderflow.master.dto.WarehouseUpdateDTO; // DTO 클래스명 통일 반영

import com.youthcase.orderflow.master.service.WarehouseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WarehouseService 통합 테스트 클래스
 * 실제 DB 연동 및 Service 로직을 검증합니다.
 */
@SpringBootTest
@Transactional // 테스트 후 DB 변경 사항을 자동으로 롤백하여 DB 오염을 방지합니다.
class WarehouseServiceTests {

    // Spring 컨테이너에 의해 WarehouseService의 구현체(Impl)가 주입됩니다.
    @Autowired
    private WarehouseService warehouseService;

    // 테스트에 사용할 샘플 창고 데이터 생성 헬퍼 메소드
    private Warehouse createSampleWarehouse(String id, String condition) {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseId(id);
        warehouse.setStorageCondition(condition);
        warehouse.setMaxCapacity(500.0);
        warehouse.setCurrentCapacity((double) 0);
        warehouse.setSpotId(1L);
        return warehouse;
    }

    // ====================================================================
    // 1. Create & Read 테스트
    // ====================================================================
    @Test
    void createAndGetWarehouseTest() {
        // Given: 새로운 창고 데이터
        String testId = "WH001";
        Warehouse newWarehouse = createSampleWarehouse(testId, "냉장");

        // When: 창고 등록 (Create)
        Warehouse createdWarehouse = warehouseService.createWarehouse(newWarehouse);

        // Then 1: 등록된 창고의 ID가 일치하는지 확인 (Create 검증)
        assertNotNull(createdWarehouse.getWarehouseId());
        assertEquals(testId, createdWarehouse.getWarehouseId());

        // When: ID로 창고 조회 (Read)
        Warehouse foundWarehouse = warehouseService.getWarehouseById(testId);

        // Then 2: 조회된 창고 데이터가 일치하는지 확인
        assertEquals("냉장", foundWarehouse.getStorageCondition());
        assertEquals(500, foundWarehouse.getMaxCapacity());
    }

    // ====================================================================
    // 2. Read All 테스트
    // ====================================================================
    @Test
    void getAllWarehousesTest() {
        // Given: 테스트를 위한 2개의 창고 데이터 등록
        warehouseService.createWarehouse(createSampleWarehouse("WH002", "실온"));
        warehouseService.createWarehouse(createSampleWarehouse("WH003", "냉동"));

        // When: 전체 목록 조회
        List<Warehouse> warehouses = warehouseService.getAllWarehouses();

        // Then: 최소 2개 이상의 데이터가 있는지 확인 (DB에 기존 데이터가 있다면 더 많을 수 있음)
        assertTrue(warehouses.size() >= 2);
    }

    // ====================================================================
    // 3. Update 테스트
    // ====================================================================
    @Test
    void updateWarehouseTest() {
        // Given: 기존 창고 등록
        String testId = "WH004";
        warehouseService.createWarehouse(createSampleWarehouse(testId, "실온"));

        // Given: 수정 데이터 (WarehouseUpdateDTO 사용)
        WarehouseUpdateDTO updateDto = new WarehouseUpdateDTO();
        updateDto.setStorageCondition("냉동"); // 보관 상태 변경
        updateDto.setMaxCapacity(999.0);      // 최대 용량 변경
        updateDto.setSpotId(2L);            // 지점 ID 변경

        // When: 창고 정보 수정
        // Service 시그니처가 DTO를 받도록 변경되었음을 가정함:
        // updateWarehouse(String warehouseId, WarehouseUpdateDTO updateDto)
        Warehouse updatedWarehouse = warehouseService.updateWarehouse(testId, updateDto);

        // Then: 수정된 내용이 반영되었는지 확인
        assertEquals("냉동", updatedWarehouse.getStorageCondition());
        assertEquals(999, updatedWarehouse.getMaxCapacity());
        assertEquals(2L, updatedWarehouse.getSpotId());
    }

    // ====================================================================
    // 4. Delete 테스트
    // ====================================================================
    @Test
    void deleteWarehouseTest() {
        // Given: 삭제할 창고 등록
        String testId = "WH005";
        warehouseService.createWarehouse(createSampleWarehouse(testId, "냉장"));

        // When: 창고 삭제
        warehouseService.deleteWarehouse(testId);

        // Then: 삭제된 창고를 조회했을 때 NoSuchElementException 예외가 발생하는지 확인
        assertThrows(NoSuchElementException.class, () -> {
            warehouseService.getWarehouseById(testId);
        });
    }
}