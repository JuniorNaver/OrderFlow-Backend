package com.youthcase.orderflow.sd.sdPayment.repository;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import com.youthcase.orderflow.sd.sdPayment.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentHeaderRepository extends JpaRepository<PaymentHeader, Long> {

    //결제 상태별 조회
    List<PaymentHeader> findByPaymentStatus(PaymentStatus status);

    //주문 단위 결제 내역 조회(헤더 단위)
    Optional<PaymentHeader> findFirstBySalesHeader_OrderIdOrderByPaymentIdDesc(Long orderId);

}
