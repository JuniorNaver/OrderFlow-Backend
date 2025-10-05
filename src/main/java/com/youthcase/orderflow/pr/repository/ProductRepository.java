package com.youthcase.orderflow.pr.repository;

import com.youthcase.orderflow.pr.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    // 전체 조회(페이지) + 카테고리 같이 로딩
    @EntityGraph(attributePaths = "category")
    Page<Product> findAll(Pageable pageable);   // JpaRepository 기본 findAll(Pageable)을 오버라이드

    // 이름 부분검색
    @EntityGraph(attributePaths = "category")
    Page<Product> findByProductNameContainingIgnoreCase(String name, Pageable pageable);

    // GTIN 부분검색
    @EntityGraph(attributePaths = "category")
    Page<Product> findByGtinContaining(String partialGtin, Pageable pageable);

    // 카테고리 코드로 조회
    @EntityGraph(attributePaths = "category")
    Page<Product> findByCategory_KanCode(String KanCode, Pageable pageable);
}
