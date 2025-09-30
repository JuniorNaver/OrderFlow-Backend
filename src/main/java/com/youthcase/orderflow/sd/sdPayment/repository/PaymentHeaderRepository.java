package com.youthcase.orderflow.sd.sdPayment.repository;

import com.youthcase.orderflow.sd.sdPayment.domain.PaymentHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentHeaderRepository extends JpaRepository<PaymentHeader, Long> {
}
