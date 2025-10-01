package com.youthcase.orderflow.po.repository;

import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.domain.POStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface POItemRepository extends JpaRepository<POItem, Long> {

    Optional<POItem> findByProductGtinAndStatus(Long gtin, String status);

    List<POItem> findAllByStatus(POStatus status);

    // 특정 발주 헤더 PK로 아이템 전체 조회
    List<POItem> findByPoHeader_PoId(Long poId);
}