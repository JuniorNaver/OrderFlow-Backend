package com.youthcase.orderflow.sd.sdSales.repository;

import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesHeaderRepository extends JpaRepository<SalesHeader, Long> {
    List<SalesHeader> findBySalesStatus(String status);
}

