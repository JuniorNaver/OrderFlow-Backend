package com.youthcase.orderflow.pr.repository;

import com.youthcase.orderflow.pr.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByKanCode(String categoryCode);

    boolean existsByKanCode(String kanCode);

    // 자식 존재 체크
    boolean existsByParent_KanCode(String kanCode);

    // 자식들 일괄 리홈
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE CATEGORY SET PARENT_KAN_CODE = :toKan WHERE PARENT_KAN_CODE = :fromKan", nativeQuery = true)
    int rehomeChildren(@Param("fromKan") String fromKan, @Param("toKan") String toKan);
}