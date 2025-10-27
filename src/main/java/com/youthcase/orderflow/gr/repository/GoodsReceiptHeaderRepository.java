package com.youthcase.orderflow.gr.repository;

import com.youthcase.orderflow.gr.domain.GoodsReceiptHeader;
import com.youthcase.orderflow.gr.dto.GRListDTO;
import com.youthcase.orderflow.gr.status.GoodsReceiptStatus;
import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GoodsReceiptHeaderRepository extends JpaRepository<GoodsReceiptHeader, Long> {

    /**
     * 아이템을 함께 로드하는 입고헤더 조회 (지연 로딩 방지)
     */
    @EntityGraph(attributePaths = {"items", "warehouse", "poHeader", "user"})
    Optional<GoodsReceiptHeader> findWithItemsByGrHeaderId(Long grHeaderId);


    /**
     * 특정 사용자 기준으로 입고내역 조회
     */
    @EntityGraph(attributePaths = {"items"})
    List<GoodsReceiptHeader> findByUser_UserId(String userId);

    /**
     * 발주내역 기준으로 입고내역 조회 (POHeader 연결)
     */
    @EntityGraph(attributePaths = {"items"})
    Optional<GoodsReceiptHeader> findByPoHeader_PoId(Long poId);

    List<GoodsReceiptHeader> findByStatus(GoodsReceiptStatus status);

    @Query("""
    SELECT new com.youthcase.orderflow.gr.dto.GRListDTO(
        gr.grHeaderId,
        po.poId,
        po.externalId,
        po.totalAmount,
        COALESCE(SUM(i.orderQty), 0L),
        u.name,
        CASE
            WHEN gr.status IS NOT NULL THEN gr.status
            WHEN po.status IN (
                com.youthcase.orderflow.po.domain.POStatus.PO,
                com.youthcase.orderflow.po.domain.POStatus.GI,
                com.youthcase.orderflow.po.domain.POStatus.PARTIAL_RECEIVED
            ) THEN :pendingStatus
            ELSE NULL
        END,
        COALESCE(gr.receiptDate, po.actionDate),
        MAX(i.expectedArrival)
    )
    FROM POHeader po
    LEFT JOIN po.items i
    LEFT JOIN po.user u
    LEFT JOIN GoodsReceiptHeader gr ON gr.poHeader.poId = po.poId
    WHERE po.status NOT IN (:deletedStatus, :draftStatus)
    AND (
        gr.status IS NOT NULL
        OR po.status IN (
            com.youthcase.orderflow.po.domain.POStatus.PO, 
            com.youthcase.orderflow.po.domain.POStatus.GI,
            com.youthcase.orderflow.po.domain.POStatus.PARTIAL_RECEIVED
        )
    )
    GROUP BY gr.grHeaderId, po.poId, po.externalId, po.totalAmount,
             u.name, gr.status, gr.receiptDate, po.actionDate, po.status
    ORDER BY po.poId DESC
""")
    List<GRListDTO> findAllWithPOStatus(
            @Param("deletedStatus") POStatus deletedStatus,
            @Param("draftStatus") POStatus draftStatus,
            @Param("pendingStatus") GoodsReceiptStatus pendingStatus
    );

    // 단순 기간 조회
    List<GoodsReceiptHeader> findByReceiptDateBetween(LocalDate start, LocalDate end);

    // 키워드 + 기간 검색 (상품명, 코드, 비고)
    @Query("""
        SELECT DISTINCT h 
        FROM GoodsReceiptHeader h 
        JOIN h.items i 
        JOIN i.product p
        WHERE h.receiptDate BETWEEN :start AND :end
          AND (
            LOWER(p.productName) LIKE LOWER(CONCAT('%', :query, '%')) OR
            LOWER(p.gtin) LIKE LOWER(CONCAT('%', :query, '%')) OR
            LOWER(h.note) LIKE LOWER(CONCAT('%', :query, '%'))
          )
        ORDER BY h.receiptDate DESC
        """)
    List<GoodsReceiptHeader> searchByKeywordAndDate(
            @Param("query") String query,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    // POHeader 기준 조회
    boolean existsByPoHeader(POHeader po);
}
