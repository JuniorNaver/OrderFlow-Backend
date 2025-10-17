package com.youthcase.orderflow.pr.repository;

import com.youthcase.orderflow.pr.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProduct_Gtin(String gtin);

    // 여러 GTIN 한 번에
    List<Inventory> findAllByProduct_GtinIn(Collection<String> gtins);

    // 가용 재고가 0 초과인 것만
    List<Inventory> findAllByOnHandGreaterThanAndReservedLessThan(Integer on, Integer r);

    // 상품당 재고가 꼭 1행이어야 한다면 존재 여부
    boolean existsByProduct_Gtin(String gtin);
}
