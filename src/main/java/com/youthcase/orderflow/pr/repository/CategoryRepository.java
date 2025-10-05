package com.youthcase.orderflow.pr.repository;

import com.youthcase.orderflow.pr.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByKanCode(String categoryCode);

}