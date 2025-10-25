package com.youthcase.orderflow.po.repository;

import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.domain.POStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface POItemRepository extends JpaRepository<POItem, Long> {

    /** 특정 헤더 내 모든 아이템 조회 */
    List<POItem> findByPoHeader_PoId(Long poId); // ✅ [getAllItems(), calculateTotalAmountForHeader() 등]

    /** 상품번호 + 상태로 단일 아이템 조회 */
    Optional<POItem> findByItemNoAndStatus(Long itemNo, POStatus status); // ✅ [updateItemQuantity()]

    /** 동일 헤더 내 GTIN 중복 체크 */
    Optional<POItem> findByPoHeader_PoIdAndProduct_Gtin(Long poId, String gtin); // ✅ [addOrCreatePOItem()]
}
