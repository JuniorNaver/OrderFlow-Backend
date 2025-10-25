package com.youthcase.orderflow.pr.repository;

import com.youthcase.orderflow.pr.domain.ShopList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

    @Repository
    public interface ShopListRepository extends JpaRepository<ShopList, Long> {

        /* ===== 기본 조회 (product 즉시 로딩으로 N+1 방지) ===== */
        @EntityGraph(attributePaths = "product")
        Page<ShopList> findAll(Pageable pageable);

        @EntityGraph(attributePaths = "product")
        Page<ShopList> findByOrderableTrue(Pageable pageable);

        @EntityGraph(attributePaths = "product")
        Optional<ShopList> findByProduct_Gtin(String gtin);

        boolean existsByProduct_Gtin(String gtin);

        /* ===== 검색 (상품명/설명) + 옵션 필터 ===== */
        @EntityGraph(attributePaths = {"product","product.category"})
        @Query("""
          select sl
          from ShopList sl
          join sl.product p
          left join p.category c
          where (:name is null or :name = '' or lower(p.productName) like lower(concat('%', :name, '%')))
            and (:gtin is null or :gtin = '' or p.gtin like concat('%', :gtin, '%'))
            and (:categoryCode is null or :categoryCode = '' or c.kanCode = :categoryCode)
          """)
        Page<ShopList> findByFilters(
                @Param("name") String name,
                @Param("gtin") String gtin,
                @Param("categoryCode") String categoryCode,
                Pageable pageable
        );

        /* ===== 기간 필터 ===== */
        @EntityGraph(attributePaths = "product")
        Page<ShopList> findByCreatedAtBetween(Instant from, Instant to, Pageable pageable);

        /* ===== 금액 통계(스냅샷) =====
           주의: avg(BigDecimal)이 구현체에 따라 Double로 나올 수 있어
           프로젝션을 사용해 null 그대로 받고, 서비스에서 기본값 보정이 안전함.
        */
        interface PriceStats {
            BigDecimal getMin();
            BigDecimal getAvg();
            BigDecimal getMax();
        }

        @Query("""
        select min(sl.purchasePrice) as min,
               avg(sl.purchasePrice) as avg,
               max(sl.purchasePrice) as max
        from ShopList sl
        """)
        PriceStats purchasePriceStats();

        /* ===== 토글/일괄 변경 ===== */
        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query("update ShopList sl set sl.orderable = :orderable where sl.product.gtin = :gtin")
        int updateOrderableByGtin(@Param("gtin") String gtin, @Param("orderable") boolean orderable);

        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query("delete from ShopList sl where sl.product.gtin = :gtin")
        int deleteByProductGtin(@Param("gtin") String gtin);

        /* ===== 리스트 전용 프로젝션(가볍게) ===== */
        interface Row {
            Long getId();
            String getDescription();
            Boolean getOrderable();
            BigDecimal getPurchasePrice();
            ProductPart getProduct();

            interface ProductPart {
                String getGtin();
                String getProductName();
                String getImageUrl();
            }
        }

        @Query("""
        select sl
        from ShopList sl
        join sl.product p
        where (:q is null or :q = ''
               or lower(p.productName) like lower(concat('%', :q, '%'))
               or lower(sl.description) like lower(concat('%', :q, '%')))
        """)
        Page<Row> findListRows(@Param("q") String q, Pageable pageable);
    }
