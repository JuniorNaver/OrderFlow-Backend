package com.youthcase.orderflow.pr.repository;

import com.youthcase.orderflow.pr.domain.Product;
import com.youthcase.orderflow.pr.domain.StorageMethod;
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
        String getName();
        Long getProductCount();
    }

    @Query("""
  select coalesce(c.mediumCategory, c.largeCategory) as cornerName,
         count(distinct c.kanCode) as categoryCount
  from Product p join p.category c
  where (:sm is null or p.storageMethod = :sm)
  group by coalesce(c.mediumCategory, c.largeCategory)
  order by cornerName
""")
    List<Object[]> findCornersByZone(@Param("sm") StorageMethod sm);

    @Query("""
  select c.kanCode as kan,
         coalesce(c.smallCategory, c.mediumCategory, c.largeCategory) as name,
         count(p) as productCount
  from Product p join p.category c
  where (:sm is null or p.storageMethod = :sm)
    and (
      (:cornerName is null and coalesce(c.mediumCategory, c.largeCategory) is null)
      or coalesce(c.mediumCategory, c.largeCategory) = :cornerName
    )
  group by c.kanCode, coalesce(c.smallCategory, c.mediumCategory, c.largeCategory)
  order by name
""")
    List<Object[]> findKanByZoneAndCorner(@Param("sm") StorageMethod sm,
                                          @Param("cornerName") String cornerName);

    // 기타: 대분류(largeCategory)로 코너 묶기
    @Query("""
  select c.largeCategory as cornerName,
         count(distinct c.kanCode) as categoryCount
  from Product p join p.category c
  where (:sm is null or p.storageMethod = :sm)
  group by c.largeCategory
  order by cornerName
""")
    List<Object[]> findCornersByZoneUsingLarge(@Param("sm") StorageMethod sm);

    // 기타: 대분류 이름으로 KAN 묶기
    @Query("""
  select c.kanCode as kan,
         coalesce(c.smallCategory, c.mediumCategory, c.largeCategory) as name,
         count(p) as productCount
  from Product p join p.category c
  where (:sm is null or p.storageMethod = :sm)
    and (
      (:cornerName is null and c.largeCategory is null)
      or c.largeCategory = :cornerName
    )
  group by c.kanCode, coalesce(c.smallCategory, c.mediumCategory, c.largeCategory)
  order by name
""")
    List<Object[]> findKanByZoneAndLargeCorner(@Param("sm") StorageMethod sm,
                                               @Param("cornerName") String cornerName);


}
