package com.youthcase.orderflow.pr.repository;

import com.youthcase.orderflow.pr.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // TODO: 필요 시 커스텀 쿼리 추가
}
