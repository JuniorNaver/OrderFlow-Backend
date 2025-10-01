package com.youthcase.orderflow.sd.sdRefund.repository;

import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundHeaderRepository extends JpaRepository<RefundHeader, Long> {
    List<RefundHeader> findByOrderId(Long orderId);
}
