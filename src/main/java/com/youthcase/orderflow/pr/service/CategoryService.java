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
        categoryRepository.deleteById(kanCode);
    }
}