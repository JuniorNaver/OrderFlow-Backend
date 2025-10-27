package com.youthcase.orderflow.po.repository;

import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 📘 POHeaderRepository
 * - 발주 헤더(PO_HEADER) 테이블 전용 Repository
 * - PR(준비), S(저장), PO(확정), GI(출고), FULLY_RECEIVED(입고완료) 등 상태 기반 조회 중심
 * - PO 모듈과 GR(입고) 모듈에서 모두 사용됨
 */
@Repository
public interface POHeaderRepository extends JpaRepository<POHeader, Long> {

    // ----------------------------------------------------------------------
    // ✅ [1] 상태 기반 리스트 조회
    // ----------------------------------------------------------------------
    /**
     * 특정 상태(예: S, PO 등)에 해당하는 헤더 여러 건 조회
     * - 주로 "저장된 장바구니(S)" 조회 시 사용
     * - 사용처: POServiceImpl.getSavedCartList()
     */
    // ✅ 단일 상태 조회
    List<POHeader> findByStatus(POStatus status);

    // ✅ 다중 상태 조회 (in 절)
    List<POHeader> findByStatusIn(Collection<POStatus> statuses);


    // ----------------------------------------------------------------------
    // ✅ [2] 상태 기반 최신순 목록 조회
    // ----------------------------------------------------------------------
    /**
     * PR 상태 헤더를 actionDate 기준으로 최신순 정렬하여 조회
     * - 공용 장바구니 환경에서 가장 최근 PR(진행 중 장바구니)을 식별
     * - 사용처: POServiceImpl.getCurrentCartId()
     */
    @Query("""
        SELECT h
        FROM POHeader h
        WHERE h.status = :status
        ORDER BY h.actionDate DESC
    """)
    List<POHeader> findRecentByStatus(@Param("status") POStatus status);


    // ----------------------------------------------------------------------
    // ✅ [3] 상태 일괄 변경 (특정 헤더 제외)
    // ----------------------------------------------------------------------
    /**
     * 특정 헤더(excludePoId)를 제외하고, 동일 상태(oldStatus)의 나머지를 새 상태(newStatus)로 전환
     * - 예: PR 상태가 여러 개 존재할 때, 최신 1건만 남기고 나머지를 S(저장) 상태로 자동 전환
     * - 사용처: POServiceImpl.getCurrentCartId()
     */
    @Modifying
    @Transactional
    @Query("""
        UPDATE POHeader h
        SET h.status = :newStatus
        WHERE h.status = :oldStatus
          AND h.poId <> :excludePoId
    """)
    void updateStatusExceptOne(@Param("oldStatus") POStatus oldStatus,
                               @Param("newStatus") POStatus newStatus,
                               @Param("excludePoId") Long excludePoId);


    // ----------------------------------------------------------------------
    // ✅ [4] 바코드 기반 헤더+아이템 로딩 (입고 모듈에서 사용)
    // ----------------------------------------------------------------------
    /**
     * 외부 바코드(externalId)로 발주 헤더 + 아이템을 함께 조회 (EAGER FETCH)
     * - 입고(GR) 모듈에서 스캔 시 즉시 조회용으로 사용됨
     * - 사용처: GoodsReceiptService.searchPOForGR()
     */
    @Query("""
        SELECT p
        FROM POHeader p
        LEFT JOIN FETCH p.items
        WHERE p.externalId = :barcode
    """)
    Optional<POHeader> findByBarcodeWithItems(@Param("barcode") String barcode);


    // ----------------------------------------------------------------------
    // ✅ [5] 바코드 시퀀스 계산 (일자 + 지점코드 기준)
    // ----------------------------------------------------------------------
    /**
     * 특정 일자(actionDate)와 점포코드(branchCode) 기준으로
     * 생성된 발주 건수를 계산 → 외부 식별자(externalId) 시퀀스 부여용
     * - 예: 20251026 + 점포ID + 일자별 일련번호(01, 02, …)
     * - 사용처: POServiceImpl.addOrCreatePOItem()
     */
    @Query("""
        SELECT COUNT(p)
        FROM POHeader p
        WHERE p.actionDate = :actionDate
          AND p.externalId LIKE CONCAT('%', :branchCode, '%')
    """)
    long countByActionDateAndBranchCode(@Param("actionDate") LocalDate actionDate,
                                        @Param("branchCode") String branchCode);
}
