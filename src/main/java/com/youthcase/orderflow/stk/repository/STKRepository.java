package com.youthcase.orderflow.stk.repository;

import com.youthcase.orderflow.stk.domain.STK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface STKRepository extends JpaRepository<STK, Long> {

    // 1. 전체 재고의 수량(quantity) 합계를 구하는 메서드
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM STK s WHERE s.status NOT IN ('DISPOSED', 'INACTIVE')")
    Long sumActiveQuantity();

    // 2. 유통기한 만료 재고 조회
    @Query("SELECT s FROM STK s JOIN s.lot l " +
            "WHERE s.status = 'ACTIVE' AND l.expDate < :targetDate")
    List<STK> findExpiredActiveStockBefore(@Param("targetDate") LocalDate targetDate);

    // 3. 유통기한 임박 재고 조회
    /**
     * 현재 활성(ACTIVE) 상태의 재고 중 유통기한이 (오늘 ~ limitDate) 사이에 있는 재고를 조회합니다.
     * @param limitDate 임박 기준일 (예: 오늘 + 90일)
     */
    @Query("SELECT s FROM STK s JOIN s.lot l " +
            "WHERE s.status = 'ACTIVE' AND l.expDate <= :limitDate AND l.expDate >= CURRENT_DATE")
    List<STK> findNearExpiryActiveStock(@Param("limitDate") LocalDate limitDate);

    // 4. 특정 상품의 재고를 유통기한 오름차순으로 조회 (수량 > 0)
    List<STK> findByProduct_GtinAndQuantityGreaterThanOrderByLot_ExpDateAsc(String gtin, int quantity);

    // 5. 상품명으로 재고 검색 (대소문자 무시, 부분 일치)
    List<STK> findByProduct_ProductNameContainingIgnoreCase(String name);

    @Query("SELECT s FROM STK s " +
            "JOIN FETCH s.product p " +
            "JOIN FETCH s.lot l " +
            "JOIN FETCH s.warehouse w " +
            "LEFT JOIN FETCH s.goodsReceipt gr")
    List<STK> findAllWithDetails();

    /**
     * 위치 변경이 필요한 재고를 조회하는 쿼리 메서드 (예시)
     */
    List<STK> findByIsRelocationNeededTrue();

    Optional<STK> findTopByProduct_Gtin(String gtin);

    /**
     * 특정 창고/지점의 모든 활성 재고(STK)를 조회하고, 제품(Product)의 GTIN과 Lot의 유통기한(EXP_DATE) 순으로 정렬합니다.
     * 이를 통해 FIFO 위배 검사를 위한 데이터를 준비합니다.
     */
    @Query("SELECT s FROM STK s " +
            "JOIN FETCH s.lot l " +
            "JOIN FETCH s.product p " +
            "WHERE s.warehouse.warehouseId = :warehouseId AND s.quantity > 0 " +
            "ORDER BY p.gtin ASC, l.expDate ASC")
    List<STK> findActiveStocksForFifoCheck(@Param("warehouseId") Long warehouseId);
}