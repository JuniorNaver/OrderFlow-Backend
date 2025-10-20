package com.youthcase.orderflow.master.price.repository;

import com.youthcase.orderflow.master.price.domain.Price;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<Price, String> {

    /** GTIN 으로 가격 조회 */
    Optional<Price> findByProduct_Gtin(String gtin);

}