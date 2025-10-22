package com.youthcase.orderflow.gr.repository;

import com.youthcase.orderflow.gr.domain.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {
    // 상품별 LOT 전체 조회 (FEFO나 폐기 알림용)
    List<Lot> findByProduct_GtinOrderByExpDateAsc(String gtin);

    // 특정 상품 + 유효 LOT 조회
    Optional<Lot> findFirstByProduct_GtinAndStatusOrderByExpDateAsc(String gtin, Lot.LotStatus status);
}