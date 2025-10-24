package com.youthcase.orderflow.po.repository;

import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.domain.POStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface POItemRepository extends JpaRepository<POItem, Long> {

    //발주 id로 상품 조회
    List<POItem> findByPoHeader_PoId(Long poId);

    //상품 no, 상태로 단일 상품 조회
    Optional<POItem> findByItemNoAndStatus(Long itemNo, POStatus status);
//
//    Optional<POItem> findByPoHeaderAndGtin(POHeader poHeader, Product gtin);
//
//    //gtin 으로 찾기
//    Optional<POItem> findByGtin(Product gtin);


    /** PR 상태인 헤더 중 동일 GTIN 상품이 있는지 확인 */
    @Query("""
        SELECT i
        FROM POItem i
        WHERE i.poHeader.status = :status
          AND i.product.gtin = :gtin
    """)
    Optional<POItem> findByStatusAndGtin(
            @Param("status") POStatus status,
            @Param("gtin") String gtin
    );

}