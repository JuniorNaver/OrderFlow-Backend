package com.youthcase.orderflow.sd.sdReceipt.repository;

import com.youthcase.orderflow.sd.sdReceipt.domain.Receipt;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    // ✅ 날짜별 영수증 조회 (N+1 방지용 FETCH JOIN)
    @Query("""
        SELECT DISTINCT r
        FROM Receipt r
        LEFT JOIN FETCH r.store s
        LEFT JOIN FETCH r.salesHeader sh
        LEFT JOIN FETCH sh.salesItems si
        LEFT JOIN FETCH r.paymentHeader ph
        LEFT JOIN FETCH ph.paymentItems pi
        LEFT JOIN FETCH r.refundHeader rf
        WHERE r.issuedAt BETWEEN :start AND :end
        ORDER BY r.issuedAt DESC
    """)
    List<Receipt> findByIssuedDateRange(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    // ✅ 영수증 번호로 조회 (재발행용, 결제/판매 모두 포함)
    @Query("""
        SELECT DISTINCT r
        FROM Receipt r
        LEFT JOIN FETCH r.store s
        LEFT JOIN FETCH r.salesHeader sh
        LEFT JOIN FETCH sh.salesItems si
        LEFT JOIN FETCH r.paymentHeader ph
        LEFT JOIN FETCH ph.paymentItems pi
        LEFT JOIN FETCH r.refundHeader rf
        WHERE r.receiptNo = :receiptNo
    """)
    Optional<Receipt> findByReceiptNo(@Param("receiptNo") String receiptNo);

    // ✅ 50일 이전 영수증 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM Receipt r WHERE r.issuedAt < :cutoff")
    int deleteOldReceipts(@Param("cutoff") LocalDateTime cutoff);

    boolean existsByReceiptNo(String receiptNo);
}