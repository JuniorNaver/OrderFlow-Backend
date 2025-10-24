package com.youthcase.orderflow.gr.repository;

import com.youthcase.orderflow.gr.domain.STKHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StkHistoryRepository extends JpaRepository<STKHistory, Long> {
}
