package com.youthcase.orderflow.sd.sdSales.repository;

import com.youthcase.orderflow.sd.sdSales.domain.MmStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MmStockRepository extends JpaRepository<MmStock,Long> {
    Optional<MmStock> findByProduct_Gtin(String gtin);
}
