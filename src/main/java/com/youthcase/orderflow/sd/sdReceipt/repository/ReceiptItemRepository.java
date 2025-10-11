package com.youthcase.orderflow.sd.sdReceipt.repository;

import com.youthcase.orderflow.sd.sdReceipt.domain.ReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, Long> {
}
