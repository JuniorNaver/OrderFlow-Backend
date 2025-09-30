package com.youthcase.orderflow.po.repository;

import com.youthcase.orderflow.po.domain.POItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface POItemRepository extends JpaRepository<POItem, Long> {

    /**
     * 장바구니에서 SKU(상품 코드) 중복 여부 확인
     */
    Optional<POItem> findByProductGtin(String gtin);

    /**
     * 장바구니에서 SKU(상품 코드)로 아이템 삭제
     */
    void deleteByProductGtin(String gtin);
}