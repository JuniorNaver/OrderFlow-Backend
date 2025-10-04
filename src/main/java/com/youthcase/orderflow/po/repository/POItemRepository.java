package com.youthcase.orderflow.po.repository;

import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface POItemRepository extends JpaRepository<POItem, Long> {

    //발주헤더 id로 아이템 조회
    List<POItem> findByPoHeader_PoIdAndStatus(Long poId, Status status);

    //상품코드를 통한 여러 상품 조회
    List<POItem> findAllByGtinIn(List<Long> gtins);



    //특정 상태에 해당하는 아이템 조회
    List<POItem> findAllByStatus(Status status);

    //상품코드와 상태로 아이템 조회.
    Optional<POItem> findByGtinAndStatus(Long gtin, Status status);

}