package com.youthcase.orderflow.sd.repository;

import com.youthcase.orderflow.sd.domain.SalesItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesItemRepository extends JpaRepository<SalesItem, Long> {
}
