package com.youthcase.orderflow.po.repository;

import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface POHeaderRepository extends JpaRepository<POHeader, Long> {
    // 기본적인 save(), findById(), delete() 같은 CRUD 메서드는 JpaRepository가 제공하므로 추가할 필요 없음

    /** 상태로 헤더 목록 조회 (아이템을 누를 때 헤더를 추가할지, 넣을 헤더가 이미 있는지)*/
    Optional<POHeader> findByStatus(POStatus status);



    /** 상태 업데이트 (예: PR → S 로 변경) */
    @Modifying
    @Transactional
    @Query("UPDATE POHeader h SET h.status = :status WHERE h.poId = :poId")
    void updateStatus(@Param("poId") Long poId, @Param("status") POStatus status);

    @Query("SELECT p FROM POHeader p LEFT JOIN FETCH p.items WHERE p.externalId = :barcode")
    Optional<POHeader> findByBarcodeWithItems(@Param("barcode") String barcode);
    /** 입고 스캔용 바코드 번호 */
    @Query("""
       SELECT COUNT(p)
       FROM POHeader p
       WHERE p.actionDate = :actionDate
         AND p.externalId LIKE CONCAT('%', :branchCode, '%')
       """)
    long countByActionDateAndBranchCode(@Param("actionDate") LocalDate actionDate,
                                        @Param("branchCode") String branchCode);



}