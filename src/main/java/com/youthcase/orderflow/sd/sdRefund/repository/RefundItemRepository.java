package com.youthcase.orderflow.sd.sdRefund.repository;

import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RefundItemRepository extends JpaRepository<RefundItem, Long> {
    // ✅ 결제수단별 환불 총액 집계
    @Query("""
        SELECT r.paymentItem.paymentMethod, SUM(r.refundAmount)
        FROM RefundItem r
        GROUP BY r.paymentItem.paymentMethod
    """)
    List<Object[]> getTotalRefundAmountByPaymentMethod();

    @Query("""
    SELECT r.paymentItem.paymentMethod, SUM(r.refundAmount)
    FROM RefundItem r
    WHERE r.refundHeader.requestedTime BETWEEN :start AND :end
    GROUP BY r.paymentItem.paymentMethod
""")
    List<Object[]> getTotalRefundAmountByPaymentMethodBetween(LocalDateTime start, LocalDateTime end);
}
