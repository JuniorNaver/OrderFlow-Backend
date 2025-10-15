package com.youthcase.orderflow.stk.repository;

import com.youthcase.orderflow.stk.domain.STK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate; // ⭐️ java.time.LocalDate 타입으로 변경
import java.util.List;

@Repository
public interface STKRepository extends JpaRepository<STK, Long> {

    // 1. 전체 재고의 수량(quantity) 합계를 구하는 메서드 (이전에 Service에서 요청한 메서드 추가)
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM STK s WHERE s.status NOT IN ('DISPOSED', 'INACTIVE')")
    Long sumActiveQuantity(); // ⭐️ COALESCE로 null 대신 0 반환, 이름 변경

    // 2. 유통기한 만료 재고 조회
    @Query("SELECT s FROM STK s JOIN s.lot l " +
            "WHERE s.status = 'ACTIVE' AND l.expDate < :targetDate")
    List<STK> findExpiredActiveStockBefore(@Param("targetDate") LocalDate targetDate); // ⭐️ 타입 변경

    // 3. 유통기한 임박 재고 조회
    /**
     * 현재 활성(ACTIVE) 상태의 재고 중 유통기한이 (오늘 ~ limitDate) 사이에 있는 재고를 조회합니다.
     * @param limitDate 임박 기준일 (예: 오늘 + 90일)
     */
    @Query("SELECT s FROM STK s JOIN s.lot l " +
            "WHERE s.status = 'ACTIVE' AND l.expDate <= :limitDate AND l.expDate >= CURRENT_DATE")
    List<STK> findNearExpiryActiveStock(@Param("limitDate") LocalDate limitDate);
    // ⭐️ @Param("targetDate") 제거 및 쿼리에서 CURRENT_DATE(오늘) 사용

    // 4. 특정 상품의 재고를 유통기한 오름차순으로 조회 (수량 > 0)
    List<STK> findByProduct_GtinAndQuantityGreaterThanOrderByLot_ExpDateAsc(String gtin, int quantity);
}