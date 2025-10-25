package com.youthcase.orderflow.sd.sdReceipt.repository;

import com.youthcase.orderflow.sd.sdReceipt.domain.Receipt;
import org.springframework.transaction.annotation.Transactional;
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
            SELECT DISTINCT r FROM Receipt r
            JOIN FETCH r.salesHeader sh
            LEFT JOIN FETCH sh.salesItems si
            LEFT JOIN FETCH si.product p
            JOIN FETCH r.paymentHeader ph
            LEFT JOIN FETCH ph.paymentItems pi
            LEFT JOIN FETCH r.store s
            WHERE r.issuedAt BETWEEN :start AND :end
            """)
    List<Receipt> findWithDetailsByIssuedDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    // ✅ 영수증 번호로 조회 (재발행용, 결제/판매 모두 포함)
    @Query("""
            SELECT DISTINCT r
            FROM Receipt r
            LEFT JOIN FETCH r.store s
            LEFT JOIN FETCH r.salesHeader sh
            LEFT JOIN FETCH sh.salesItems si
            LEFT JOIN FETCH si.product p
            LEFT JOIN FETCH r.paymentHeader ph
            LEFT JOIN FETCH ph.paymentItems pi
            LEFT JOIN FETCH r.refundHeader rf
            WHERE r.receiptNo = :receiptNo
        """)
    Optional<Receipt> findWithDetailsByReceiptNo(@Param("receiptNo") String receiptNo);

    @Query("SELECT r FROM Receipt r WHERE r.salesHeader.orderId = :orderId")
    Optional<Receipt> findBySalesHeader(@Param("orderId") Long orderId);

    // ✅ 50일 이전 영수증 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM Receipt r WHERE r.issuedAt < :cutoff")
    int deleteOldReceipts(@Param("cutoff") LocalDateTime cutoff);

    boolean existsByReceiptNo(String receiptNo);
}