package com.youthcase.orderflow.sd.sdSales.repository;

import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.domain.SalesStatus;
import com.youthcase.orderflow.sd.sdSales.dto.SalesHeaderDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesHeaderRepository extends JpaRepository<SalesHeader, Long> {
    @Query("""
    SELECT new com.youthcase.orderflow.sd.sdSales.dto.SalesHeaderDTO(
        sh.orderId,
        sh.orderNo,
        sh.salesDate,
        (SELECT SUM(si.sdPrice * si.salesQuantity)
         FROM SalesItem si
         WHERE si.salesHeader = sh),
        sh.salesStatus
    )
    FROM SalesHeader sh
    WHERE sh.orderId = :orderId
    """)
    List<SalesHeaderDTO> findHeader(@Param("orderId") Long orderId);

    @Query("SELECT new com.youthcase.orderflow.sd.sdSales.dto.SalesHeaderDTO( " +
            "sh.orderId, sh.orderNo, sh.salesDate, sh.totalAmount, sh.salesStatus) " +
            "FROM SalesHeader sh " +
            "WHERE sh.salesStatus = 'HOLD'")
    List<SalesHeaderDTO> findHoldOrders();

    @Query(value = """
        SELECT order_no
        FROM SALES_HEADER
        WHERE order_no LIKE :datePrefix || '%'
        ORDER BY order_no DESC
        FETCH FIRST 1 ROWS ONLY
    """, nativeQuery = true)
    String findLastOrderNoByDate(@Param("datePrefix") String datePrefix);

    List<SalesHeader> findBySalesStatus(SalesStatus status);

}

