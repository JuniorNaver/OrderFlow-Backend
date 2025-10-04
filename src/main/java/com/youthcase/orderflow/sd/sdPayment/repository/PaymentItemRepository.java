package com.youthcase.orderflow.sd.sdPayment.repository;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PaymentItemRepository extends JpaRepository<PaymentItem, Long> {

    //결제 헤더 단위의 상세 항목 조회
    List<PaymentItem> findByPaymentItemId(Long PaymentItemId);

    //통계성 쿼리: 결제 수단별 총 금액 집계(리포트용)
    @Query("SELECT i.paymentMethod, SUM(i.amount) FROM PaymentItem i GROUP BY i.paymentMethod")
    List<Object[]> getPaymentSumByMethod();
}
