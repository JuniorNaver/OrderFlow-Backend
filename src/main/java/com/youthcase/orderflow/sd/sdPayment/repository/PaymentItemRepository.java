package com.youthcase.orderflow.sd.sdPayment.repository;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentItem;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PaymentItemRepository extends JpaRepository<PaymentItem, Long> {
    List<PaymentItem> findByPaymentItemId(Long PaymentItemId);
}
