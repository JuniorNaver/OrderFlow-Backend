package com.youthcase.orderflow.sd.sdPayment.repository;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdPayment.dto.PaymentMethodSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentItemRepository extends JpaRepository<PaymentItem, Long> {

    // ✅ 결제 헤더 단위의 상세 항목 조회
    List<PaymentItem> findByPaymentItemId(Long paymentItemId);

    // ✅ 통계성 쿼리: 결제 수단별 총 금액 집계
    @Query("""
        SELECT new com.youthcase.orderflow.sd.sdPayment.dto.PaymentMethodSummary(i.paymentMethod, SUM(i.amount))
        FROM PaymentItem i
        GROUP BY i.paymentMethod
    """)
    List<PaymentMethodSummary> getPaymentSumByMethod();
}