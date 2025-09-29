package com.youthcase.orderflow.sd.repository;

import com.youthcase.orderflow.sd.domain.ProductMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductMaster, String> {

    Optional<ProductMaster> findByGtin(String gtin);
}
