package com.youthcase.orderflow.sd.sdSales.repository;

import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import com.youthcase.orderflow.sd.sdSales.dto.SalesItemDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SalesItemRepository extends JpaRepository<SalesItem, Long> {

    @Query("""
            SELECT new com.youthcase.orderflow.sd.sdSales.dto.SalesItemDTO(
              si.no, 
              p.gtin,
              p.productName,
              si.sdPrice,
              si.salesQuantity,
              /* ✅ 활성 재고 총합 */
              (SELECT COALESCE(SUM(s.quantity), 0) 
                 FROM STK s 
                WHERE s.product.gtin = p.gtin 
                  AND s.status = 'ACTIVE'),
              /* 소계 */
              (si.sdPrice * si.salesQuantity)
            )
            FROM SalesItem si
            JOIN si.product p
            WHERE si.salesHeader.orderId = :orderId
            """)
    List<SalesItemDTO> findItemsByHeaderId(@Param("orderId") Long orderId);

    List<SalesItem> findBySalesHeader_OrderId(Long orderId);

    void deleteBySalesHeader(SalesHeader header);

    @Modifying
    @Query("DELETE FROM SalesItem s WHERE s.no = :id")
    void forceDeleteById(@Param("id") Long id);

    // ✅ 같은 주문 내 동일 상품 존재 여부 확인용
    @Query("SELECT si FROM SalesItem si " +
            "WHERE si.salesHeader.orderId = :orderId AND si.product.gtin = :gtin")
    SalesItem findByOrderIdAndGtin(@Param("orderId") Long orderId,
                                   @Param("gtin") String gtin);

    // ✅ 수량/소계 업데이트 (세션 유지)
    @Modifying(flushAutomatically = true)
    @Query("UPDATE SalesItem s SET s.salesQuantity = :qty, s.subtotal = :subtotal WHERE s.no = :itemId")
    int updateQuantity(@Param("itemId") Long itemId,
                       @Param("qty") int qty,
                       @Param("subtotal") BigDecimal subtotal);

    // 특정 주문 내 특정 상품의 총 판매 수량
    @Query("SELECT COALESCE(SUM(si.salesQuantity), 0) FROM SalesItem si " +
            "WHERE si.salesHeader.orderId = :orderId AND si.product.gtin = :gtin")
    int sumQuantityByOrderAndGtin(@Param("orderId") Long orderId,
                                  @Param("gtin") String gtin);

}
