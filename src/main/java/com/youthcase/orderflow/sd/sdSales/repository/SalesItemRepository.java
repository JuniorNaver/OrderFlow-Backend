package com.youthcase.orderflow.sd.sdSales.repository;

import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import com.youthcase.orderflow.sd.sdSales.dto.SalesItemDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesItemRepository extends JpaRepository<SalesItem, Long> {

    @Query("SELECT new com.youthcase.orderflow.sd.sdSales.dto.SalesItemDTO( " +
            "p.productName, " +
            "si.sdPrice, " +
            "si.salesQuantity, " +
            "COALESCE(st.quantity, 0), " +
            "si.sdPrice * si.salesQuantity) " +
            "FROM SalesItem si " +
            "JOIN si.product p " +
            "LEFT JOIN si.stk st " +
            "WHERE si.salesHeader.orderId = :orderId")
    List<SalesItemDTO> findItemsByHeaderId(@Param("orderId") Long orderId);

    List<SalesItem> findBySalesHeader_OrderId(Long orderId);

    void deleteBySalesHeader(SalesHeader header);

}

