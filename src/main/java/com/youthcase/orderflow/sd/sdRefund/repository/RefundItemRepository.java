package com.youthcase.orderflow.sd.sdRefund.repository;

import com.youthcase.orderflow.sd.sdRefund.domain.RefundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundItemRepository extends JpaRepository<RefundItem, Long> {

}
