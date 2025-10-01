package com.youthcase.orderflow.pr.repository;

import com.youthcase.orderflow.pr.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    // 추가 커스텀 쿼리가 필요하면 여기에 정의 가능
}