package com.youthcase.orderflow.stk.repository;

import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.domain.StockStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface STKRepository extends JpaRepository<STK, Long> {

    // --------------------------------------------------
    // 📦 창고별 적재 용량 합계 (CBM 단위)
    // --------------------------------------------------
    @Query("""
        SELECT s.warehouse.warehouseId,
               COALESCE(SUM(
                 (s.quantity *
                  (s.product.widthMm * s.product.depthMm * s.product.heightMm) / 1000000000.0)
               ), 0)
        FROM STK s
        WHERE s.status IN :stockedStatuses
        GROUP BY s.warehouse.warehouseId
    """)
    List<Object[]> sumCbmByWarehouse(@Param("stockedStatuses") List<StockStatus> stockedStatuses);

    // --------------------------------------------------
    // 📦 재고 조회 및 FIFO (활성 재고)
    // --------------------------------------------------

    /**
     * GTIN과 수량이 0보다 큰 활성 재고를 유통기한 순으로 조회 (FIFO 원칙)
     */
    List<STK> findByProduct_GtinAndQuantityGreaterThanOrderByLot_ExpDateAsc(String gtin, Long quantity);

    /**
     * 특정 GTIN의 재고 하나를 조회
     */
    Optional<STK> findTopByProduct_Gtin(String gtin);

    /**
     * Lot ID로 수량이 0보다 큰 활성 재고 조회 (폐기 처리 시 사용)
     */
    Optional<STK> findByLot_LotIdAndQuantityGreaterThan(Long lotId, Long quantity);

    /**
     * Lot ID로 STK 조회 (조정 처리 시 수량 0 이하도 조회하기 위해 사용)
     */
    Optional<STK> findByLot_LotId(Long lotId);

    // ✅ GTIN + 유통기한 기준으로 재고 찾기
    Optional<STK> findByProduct_GtinAndLot_ExpDate(String gtin, LocalDate expDate);

    // --------------------------------------------------
    // 🗑️ 상태 및 기간 조회
    // --------------------------------------------------

    /**
     * 특정 날짜 이전에 유통기한이 만료된 활성 재고 조회 (폐기 목록/실행)
     */
    // STK 엔티티가 Lot 엔티티를 통해 유통기한(expDate)을 참조한다고 가정
    @Query("""
        SELECT s FROM STK s JOIN s.lot l 
        WHERE l.expDate < :date 
          AND s.quantity > 0 
          AND s.status = :targetStatus
    """)
    List<STK> findExpiredActiveStockBefore(@Param("date") LocalDate targetDate,
                                           @Param("targetStatus") StockStatus targetStatus);

    /**
     * 특정 날짜까지 유통기한이 임박한 활성 재고 조회 (대시보드 현황)
     */
    @Query("""
        SELECT s FROM STK s JOIN s.lot l 
        WHERE l.expDate <= :limitDate 
          AND s.quantity > 0 
          AND s.status = :targetStatus
    """)
    List<STK> findNearExpiryActiveStock(@Param("limitDate") LocalDate limitDate,
                                        @Param("targetStatus") StockStatus targetStatus);

    /**
     * 위치 변경 필요 재고 조회
     */
    List<STK> findByIsRelocationNeededTrue();

    /**
     * 상품명으로 재고 검색
     */
    List<STK> findByProduct_ProductNameContainingIgnoreCase(String name);

    // --------------------------------------------------
    // 📊 전체 유효 재고 합계
    // --------------------------------------------------
    @Query("""
        SELECT COALESCE(SUM(s.quantity), 0)
        FROM STK s 
        WHERE s.status IN :stockedStatuses
    """)
    Long sumActiveQuantity(@Param("stockedStatuses") List<StockStatus> stockedStatuses);

    // --------------------------------------------------
    // ⚙️ 재고 조정 관련 (Quantity <= N)
    // --------------------------------------------------

    /**
     * ⭐️ 수량 조정 대상 목록 조회: 수량이 지정된 값(예: 0) 이하인 재고 항목들을 조회합니다.
     * FIFO 위반 등으로 수량 불일치가 발생한 재고를 찾을 때 사용됩니다.
     */
    List<STK> findByQuantityLessThanEqual(Long quantity);

    @Query("""
        SELECT s FROM STK s JOIN s.lot l 
        WHERE s.warehouse.warehouseId = :warehouseId 
          AND s.quantity > 0 
          AND s.status = :targetStatus
        ORDER BY l.expDate ASC
    """)
    List<STK> findActiveStocksForFifoCheck(@Param("warehouseId") String warehouseId,
                                           @Param("targetStatus") StockStatus targetStatus);

    // --------------------------------------------------
    // 📦 상품별 전체 수량
    // --------------------------------------------------
    //GTIN 전체 재고 합계 구해주는 쿼리
    @Query("""
        SELECT COALESCE(SUM(s.quantity), 0)
        FROM STK s 
        WHERE s.product.gtin = :gtin 
          AND s.status IN :stockedStatuses
    """)
    Long sumStockedQuantityByGtin(@Param("gtin") String gtin,
                                  @Param("stockedStatuses") List<StockStatus> statuses);

    // ✅ 창고 + 상품 + LOT 기준으로 조회 (중복 방지)
    @Query("""
        SELECT s FROM STK s 
        WHERE s.warehouse.warehouseId = :warehouseId 
          AND s.product.gtin = :gtin 
          AND s.lot.lotId = :lotId
    """)
    Optional<STK> findByWarehouseAndProductAndLot(@Param("warehouseId") String warehouseId,
                                                  @Param("gtin") String gtin,
                                                  @Param("lotId") Long lotId);

    // --------------------------------------------------
    // 🧩 재고 중복 확인 (창고 + 입고 + LOT 조합)
    // --------------------------------------------------
    boolean existsByWarehouse_WarehouseIdAndGoodsReceipt_IdAndLot_LotId(
            String warehouseId,
            Long goodsReceiptId,
            Long lotId
    );

    // --------------------------------------------------
    // 🧩 임시 재고 중복 확인 (창고 + 상품 + LOT + GR NULL)
    // --------------------------------------------------
    Optional<STK> findByWarehouse_WarehouseIdAndProduct_GtinAndLot_LotIdAndGoodsReceiptIsNull(
            String warehouseId,
            String gtin,
            Long lotId
    );
}