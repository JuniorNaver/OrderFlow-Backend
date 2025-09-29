package com.youthcase.orderflow.sd.repository;

import com.youthcase.orderflow.sd.domain.SalesHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesHeaderRepository extends JpaRepository<SalesHeader, Long> {
}

