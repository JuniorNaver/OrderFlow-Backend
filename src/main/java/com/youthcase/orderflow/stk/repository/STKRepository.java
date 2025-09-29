package com.youthcase.orderflow.stk.repository;

import com.youthcase.orderflow.stk.domain.STK; // domain 패키지 참조
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface STKRepository extends JpaRepository<STK, Long> {

    // JpaRepository는 기본 CRUD 메서드를 제공합니다.

    // 예시 쿼리: 특정 창고의 재고 목록 조회
    // findBy[참조 객체 이름]_[참조 객체의 PK 이름]
    List<STK> findByWarehouse_WarehouseId(String warehouseId);

    // 예시 쿼리: 고유 조건에 맞는 재고 단건 조회 (창고, 상품, LOT)
    Optional<STK> findByWarehouse_WarehouseIdAndProduct_GtinAndLot_LotId(String warehouseId, Long gtin, Long lotId);

    // (이 외 나머지 커스텀 쿼리는 여기에 추가하면 됩니다.)
}