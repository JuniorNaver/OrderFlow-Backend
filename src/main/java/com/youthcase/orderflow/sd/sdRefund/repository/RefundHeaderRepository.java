package com.youthcase.orderflow.sd.sdRefund.repository;

import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RefundHeaderRepository extends JpaRepository<RefundHeader, Long> {
    // 1️⃣ 결제건별 환불 내역 조회
    List<RefundHeader> findByPaymentHeader_PaymentId(Long paymentId);

    // 2️⃣ 환불 상태별 조회 (승인대기, 완료 등)
    List<RefundHeader> findByRefundStatus(RefundStatus refundStatus);

    // 3️⃣ 환불 요청일 기준 기간 조회 (정산용)
    List<RefundHeader> findByRequestedTimeBetween(LocalDateTime start, LocalDateTime end);

    boolean existsByPaymentHeader(com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader paymentHeader);

    // 4️⃣ 관리자 승인 대기 건 (선택)
    @Query("""
        SELECT DISTINCT r
        FROM RefundHeader r
        JOIN FETCH r.refundItems i
        WHERE r.refundStatus = 'REQUESTED'
        ORDER BY r.requestedTime ASC
    """)
    List<RefundHeader> findPendingRefundRequests();

}
