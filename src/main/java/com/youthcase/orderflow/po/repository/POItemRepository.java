package com.youthcase.orderflow.po.repository;

import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface POItemRepository extends JpaRepository<POItem, Long> {

    //특정상태에 해당하는 아이템 전부 조회
    List<POItem> findAllByStatus(Status status);

    //상품 GTIN + 상태(enum)으로 특정 아이템 조회.
    Optional<POItem> findByGtinAndStatus(Long gtin, Status status);

    //특정 발주 헤더 PK로 아이템 전체 조회
    List<POItem> findByPoHeader_PoId(Long poId);
}