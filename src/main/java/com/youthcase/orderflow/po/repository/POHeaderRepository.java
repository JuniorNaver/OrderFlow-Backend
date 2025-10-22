package com.youthcase.orderflow.po.repository;

import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface POHeaderRepository extends JpaRepository<POHeader, Long> {
    // 기본적인 save(), findById(), delete() 같은 CRUD 메서드는 JpaRepository가 제공하므로 추가할 필요 없음

    /** ✅ 상태(status)로 발주 헤더 목록 조회 */
    List<POHeader> findByStatus(POStatus status);

    /** ✅ 상태 업데이트 (예: PR → S 로 변경) */
    @Modifying
    @Transactional
    @Query("UPDATE POHeader h SET h.status = :status WHERE h.poId = :poId")
    void updateStatus(@Param("poId") Long poId, @Param("status") POStatus status);

    @Query("SELECT p FROM POHeader p LEFT JOIN FETCH p.items WHERE p.poBarcode = :barcode")
    Optional<POHeader> findByBarcodeWithItems(@Param("barcode") String barcode);


}