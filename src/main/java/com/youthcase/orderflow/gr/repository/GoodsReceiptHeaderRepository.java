package com.youthcase.orderflow.gr.repository;

import com.youthcase.orderflow.gr.domain.GoodsReceiptHeader;
import com.youthcase.orderflow.gr.status.GoodsReceiptStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoodsReceiptHeaderRepository extends JpaRepository<GoodsReceiptHeader, Long> {

    /**
     * 아이템을 함께 로드하는 입고헤더 조회 (지연 로딩 방지)
     */
    @EntityGraph(attributePaths = {"items", "warehouse", "poHeader", "user"})
    Optional<GoodsReceiptHeader> findWithItemsById(Long id);

    /**
     * 특정 창고 기준으로 입고내역 전체 조회
     */
    @EntityGraph(attributePaths = {"items"})
    List<GoodsReceiptHeader> findByWarehouse_WarehouseId(String warehouseId);

    /**
     * 특정 사용자 기준으로 입고내역 조회
     */
    @EntityGraph(attributePaths = {"items"})
    List<GoodsReceiptHeader> findByUser_UserId(String userId);

    /**
     * 발주내역 기준으로 입고내역 조회 (POHeader 연결)
     */
    @EntityGraph(attributePaths = {"items"})
    List<GoodsReceiptHeader> findByPoHeader_PoId(Long poId);

    List<GoodsReceiptHeader> findByStatus(GoodsReceiptStatus status);
}
