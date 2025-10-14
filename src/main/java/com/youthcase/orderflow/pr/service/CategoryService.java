package com.youthcase.orderflow.pr.service;

import com.youthcase.orderflow.pr.domain.Category;
import com.youthcase.orderflow.pr.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(String kanCode) {
        return categoryRepository.findById(kanCode);
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(String kanCode) {
        try {
            categoryRepository.deleteById(kanCode);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT,
                    "연결된 상품이 있어 카테고리를 삭제할 수 없습니다: " + kanCode
            );
        }
    }
}

// 수정하는거 보기