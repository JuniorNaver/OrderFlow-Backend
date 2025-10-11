package com.youthcase.orderflow.sd.sdReceipt.repository;

import com.youthcase.orderflow.sd.sdReceipt.domain.ReceiptHeader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReceiptHeaderRepository extends JpaRepository<ReceiptHeader, Long> {
    Optional<ReceiptHeader> findByPaymentHeader_PaymentId(Long paymentId);
    Optional<ReceiptHeader> findByReceiptNo(String receiptNo);
}
