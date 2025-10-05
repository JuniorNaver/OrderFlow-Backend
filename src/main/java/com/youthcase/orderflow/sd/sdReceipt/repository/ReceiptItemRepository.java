package com.youthcase.orderflow.sd.sdReceipt.repository;

import com.youthcase.orderflow.sd.sdReceipt.domain.ReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, Long> {
}