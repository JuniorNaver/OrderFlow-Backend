package com.youthcase.orderflow.stk.repository;

import com.youthcase.orderflow.stk.domain.STK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date; // java.util.Date 타입 사용
import java.util.List;

@Repository
public interface STKRepository extends JpaRepository<STK, Long> {

    List<STK> findByWarehouse_WarehouseId(String warehouseId);

    // 유통기한 만료 재고 조회 (기존 로직)
    @Query("SELECT s FROM STK s JOIN s.lot l " +
            "WHERE s.status = 'ACTIVE' AND l.expDate < :targetDate")
    List<STK> findExpiredActiveStockBefore(@Param("targetDate") Date targetDate);

    // ⭐️ 유통기한 임박 재고 조회 쿼리 추가
    /**
     * 현재 활성(ACTIVE) 상태의 재고 중 유통기한이 (오늘 + 임박일) 이내인 재고를 조회합니다.
     * l.expDate <= :limitDate 를 통해 임박 기한 내에 있는 재고를 찾습니다.
     * ⚠️ 단, 해당 쿼리는 DB의 날짜 계산 함수를 사용하는 것이 가장 효율적이나, JPQL의 단순 비교를 위해
     * 서비스 레이어에서 임박일이 적용된 '기준 날짜'를 계산하여 넘겨야 합니다.
     */
    @Query("SELECT s FROM STK s JOIN s.lot l " +
            "WHERE s.status = 'ACTIVE' AND l.expDate <= :limitDate AND l.expDate > :targetDate")
    List<STK> findNearExpiryActiveStock(@Param("limitDate") Date limitDate, @Param("targetDate") Date targetDate);

    List<STK> findByProduct_GtinAndQuantityGreaterThanOrderByLot_ExpDateAsc(String gtin, int quantity);
}