package com.youthcase.orderflow.pr.repository;

import com.youthcase.orderflow.pr.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    // GTIN으로 단건 조회 (Optional<Product> 대신 List<Product> 가능)
    Optional<Product> findByGtin(String gtin);

    // GTIN이 존재하는지 여부 확인
    boolean existsByGtin(String gtin);

    // GTIN 일부 포함해서 검색
    List<Product> findByGtinContaining(String partialGtin);
}
