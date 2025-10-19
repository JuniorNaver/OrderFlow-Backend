package com.youthcase.orderflow.master.product.repository;

import com.youthcase.orderflow.master.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    // 전체 조회(페이지) + 카테고리 같이 로딩
    @EntityGraph(attributePaths = "category")
    Page<Product> findAll(Pageable pageable);   // JpaRepository 기본 findAll(Pageable)을 오버라이드

    // 이름 부분검색
    @EntityGraph(attributePaths = "category")
    Page<Product> findByProductNameContainingIgnoreCase(String name, Pageable pageable);

    // GTIN 부분검색
    @EntityGraph(attributePaths = "category")
    Page<Product> findByGtinContaining(String partialGtin, Pageable pageable);

    // 카테고리 코드로 조회
    @EntityGraph(attributePaths = "category")
    Page<Product> findByCategory_KanCode(String KanCode, Pageable pageable);

    // 상품검색
    Optional<Product> findByGtin(String gtin);

    public interface CornerRow {
        String getCornerName();

        Long getCategoryCount();
    }

    public interface KanRow {
        String getKan();

        String getLabel();

        Long getProductCount();
    }

    @Query("""
            select c.mediumCategory as name, count(distinct c.kanCode) as cnt
            from Category c
            where c.totalCategory = :total
            group by c.mediumCategory
            order by c.mediumCategory
            """)
    List<Object[]> findCornersByTotalCategory(@Param("total") String total);


    // 기타: 대분류(largeCategory)로 코너 묶기
    @Query("""
            select c.largeCategory as name, count(distinct c.kanCode) as cnt
            from Category c
            where c.totalCategory = :total
            group by c.largeCategory
            order by c.largeCategory
            """)
    List<Object[]> findCornersByTotalCategoryUsingLarge(@Param("total") String total);

    // 중분류 코너 선택시
    @Query("""
select c.kanCode,
       coalesce(c.smallCategory, c.mediumCategory, c.largeCategory),
       count(p.gtin)
from Category c
left join Product p on p.category = c
where c.totalCategory = :total
  and (:cornerName is null or c.mediumCategory = :cornerName)
group by c.kanCode, c.smallCategory, c.mediumCategory, c.largeCategory
order by coalesce(c.smallCategory, c.mediumCategory, c.largeCategory)
""")
    List<Object[]> findKanByTotalCategoryAndCorner(@Param("total") String total,
                                                   @Param("cornerName") String cornerName);

    // 기타(대분류 코너 선택 시)
    @Query("""
select c.kanCode,
       coalesce(c.smallCategory, c.largeCategory),
       count(p.gtin)
from Category c
left join Product p on p.category = c
where c.totalCategory = :total
  and (:cornerName is null or c.largeCategory = :cornerName)
group by c.kanCode, c.smallCategory, c.largeCategory
order by coalesce(c.smallCategory, c.largeCategory)
""")
    List<Object[]> findKanByTotalCategoryAndLargeCorner(@Param("total") String total,
                                                        @Param("cornerName") String cornerName);

    Optional<Product> findByProductName(String productName);

}
