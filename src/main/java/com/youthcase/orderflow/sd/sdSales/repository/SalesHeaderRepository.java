package com.youthcase.orderflow.sd.sdSales.repository;

import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.dto.SalesHeaderDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesHeaderRepository extends JpaRepository<SalesHeader, Long> {
    @Query("SELECT new com.youthcase.orderflow.sd.sdSales.dto.SalesHeaderDTO( " +
            "sh.orderId, sh.salesDate, " +
            "(SELECT SUM(si.sdPrice * si.quantity) FROM SalesItem si WHERE si.salesHeader = sh), " +
            "p.productName, sh.salesStatus, null) " +
            "FROM SalesHeader sh " +
            "JOIN sh.salesItems si " +
            "JOIN si.product p " +
            "WHERE sh.orderId = :orderId")
    List<SalesHeaderDTO> findHeader(@Param("orderId") Long orderId);

    @Query("SELECT new com.youthcase.orderflow.sd.sdSales.dto.SalesHeaderDTO( " +
            "sh.orderId, sh.salesDate, sh.totalAmount, sh.salesStatus) " +
            "FROM SalesHeader sh " +
            "WHERE sh.salesStatus = 'HOLD'")
    List<SalesHeaderDTO> findHoldOrders();
}

