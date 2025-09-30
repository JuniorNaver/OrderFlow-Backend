package com.youthcase.orderflow.stk.service;

import com.youthcase.orderflow.stk.domain.Warehouse;
import com.youthcase.orderflow.stk.dto.WarehouseUpdateDTO;

import java.util.List;

public interface WarehouseService {

    /**
     * 신규 창고 등록 (Create)
     */
    Warehouse createWarehouse(Warehouse warehouse);

    /**
     * 전체 창고 목록 조회 (Read All)
     */
    List<Warehouse> getAllWarehouses();

    /**
     * 특정 창고 ID로 상세 정보 조회 (Read One)
     */
    Warehouse getWarehouseById(String warehouseId);

    /**
     * 기존 창고 정보 수정 (Update)
     */
    Warehouse updateWarehouse(String warehouseId, WarehouseUpdateDTO updateDto); ;

    /**
     * 창고 정보 삭제 (Delete)
     */
    void deleteWarehouse(String warehouseId);
}