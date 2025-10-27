package com.youthcase.orderflow.master.product.repository;

import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.product.domain.StorageMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    boolean existsByCategory_KanCode(String kanCode);

    //상품 일괄 리콜
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE PRODUCT SET KAN_CODE = :toKan WHERE KAN_CODE = :fromKan", nativeQuery = true)
    int rehomeProducts(@Param("fromKan") String fromKan, @Param("toKan") String toKan);

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

    //GR다중 상품검색
    List<Product> findByGtinIn(List<String> gtins);


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

    @EntityGraph(attributePaths = "category")
    @Query("""
            select p from Product p
            left join p.category c
            where p.orderable = true
              and (:zonesEmpty = true or p.storageMethod in :zones)
              and (:catsEmpty  = true or coalesce(c.smallCategory, c.mediumCategory, c.largeCategory) in :cats)
            order by
              p.productName asc,
              p.gtin asc
            """)
    List<Product> findRecommendableInternalMulti(
            @Param("cats") List<String> cats,
            @Param("zones") List<StorageMethod> zones,
            @Param("catsEmpty") boolean catsEmpty,
            @Param("zonesEmpty") boolean zonesEmpty
    );

    public interface ProductCountPerKan {
        String getKanCode();
        Long getCnt();
    }

    @Query("""
    select p.category.kanCode as kanCode, count(p) as cnt
    from Product p
    group by p.category.kanCode
""")
    List<ProductCountPerKan> countProductsGroupByKan();

    // ✅ 특정 KAN의 상품 간단 목록 (행 펼침용)
    public interface SimpleProductRow {
        String getGtin();
        String getProductName();
        BigDecimal getPrice();         // 타입은 엔티티에 맞춰서
    }

    @Query("""
    select p.gtin as gtin, p.productName as productName, p.price as price
    from Product p
    where p.category.kanCode = :kan
    order by p.productName asc, p.gtin asc
""")
    List<SimpleProductRow> findSimpleByCategoryKan(@Param("kan") String kan);

    // 카테고리별 대표상품
    public interface CategoryProductSampleProjection {
        String getKanCode();
        String getProductName();;
    }

    @Query(value = """
        SELECT T.KAN_CODE      AS kanCode,
               T.PRODUCT_NAME  AS productName
        FROM (
          SELECT p.KAN_CODE,
                 p.PRODUCT_NAME,
                 ROW_NUMBER() OVER (
                   PARTITION BY p.KAN_CODE
                   ORDER BY p.PRODUCT_NAME ASC
                 ) AS RN
          FROM PRODUCT p
          WHERE p.KAN_CODE IN (:kanCodes)
        ) T
        WHERE T.RN <= :limit
        ORDER BY T.KAN_CODE, T.PRODUCT_NAME
        """, nativeQuery = true)
    List<CategoryProductSampleProjection> findCategoryProductSamples(
            @Param("kanCodes") List<String> kanCodes,
            @Param("limit") int limit
    );


    default List<Product> findRecommendable(List<String> cats, List<StorageMethod> zones) {
        boolean catsEmpty = (cats == null || cats.isEmpty());
        boolean zonesEmpty = (zones == null || zones.isEmpty());

        // ✅ 제네릭 힌트로 List<Object> 추론 방지
        List<String> catsArg = catsEmpty  ? java.util.Collections.<String>emptyList()         : cats;
        List<StorageMethod> zonesArg = zonesEmpty ? java.util.Collections.<StorageMethod>emptyList() : zones;

        return findRecommendableInternalMulti(
                catsArg,
                zonesArg,
                catsEmpty,
                zonesEmpty
        );
    }
 };
