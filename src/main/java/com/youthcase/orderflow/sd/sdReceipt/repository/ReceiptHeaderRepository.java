package com.youthcase.orderflow.sd.sdReceipt.repository;

import com.youthcase.orderflow.sd.sdReceipt.domain.ReceiptHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReceiptHeaderRepository extends JpaRepository<ReceiptHeader, Long> {
    Optional<ReceiptHeader> findByPaymentId(Long paymentId);
}
