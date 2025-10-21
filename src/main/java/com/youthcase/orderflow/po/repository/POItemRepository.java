package com.youthcase.orderflow.po.repository;

import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.domain.POStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface POItemRepository extends JpaRepository<POItem, Long> {

    //발주 id로 상품 조회
    List<POItem> findByPoHeader_PoId(Long poId);

    //발주 id, 상태로 상품들 조회
    List<POItem> findByPoHeader_PoIdAndStatus(Long poId, POStatus status);

    //상품 no, 상태로 단일 상품 조회
    Optional<POItem> findByItemNoAndStatus(Long itemNo, POStatus status);

}