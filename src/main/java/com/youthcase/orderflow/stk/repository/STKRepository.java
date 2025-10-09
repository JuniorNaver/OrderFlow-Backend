package com.youthcase.orderflow.stk.repository;

import com.youthcase.orderflow.stk.domain.STK; // domain 패키지 참조
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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


    Optional<STK> findByWarehouseIdAndGtinAndLotId(String warehouseId, String gtin, Long lotId);
    @Query("SELECT SUM(s.quantity) FROM STK s WHERE s.product.gtin = :gtin AND s.status = 'ACTIVE'")
    Integer findTotalQuantityByGtin(@Param("gtin") String gtin);


    /**
     * [FIFO 전략] 특정 상품에 대해 재고 수량이 0보다 크고,
     * LOT의 입고일(Lot 엔티티의 receivedAt 필드 가정) 순서로 오름차순 정렬하여 재고 레코드를 조회합니다.
     */
    @Query("SELECT s FROM STK s " +
            "JOIN s.lot l " +
            "WHERE s.product.gtin = :gtin AND s.quantity > 0 AND s.status = 'ACTIVE'" +
            "ORDER BY l.receivedAt ASC") // LOT의 receivedAt (가장 먼저 들어온 재고 우선)
    List<STK> findAvailableStocksByGtinForFIFO(@Param("gtin") String gtin);

    // ⭐ 특정 창고에서만 출고 시: findAvailableStocksByGtinAndWarehouseIdForFIFO 메서드 추가 가능
}