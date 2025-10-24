package com.youthcase.orderflow.pr.service;

import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.pr.domain.Category;
import com.youthcase.orderflow.pr.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    // 기타 카테고리(리홈 대상) 고정 값
    private static final String DEFAULT_KAN = "99999999";

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(String kanCode) {
        return categoryRepository.findById(kanCode);
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(String kanCode) {
        // 기본 카테고리 보호
        if (DEFAULT_KAN.equals(kanCode)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, " 기본 카테고리는 삭제할 수 없습니다.");
        }

        // 1) 존재 확인
        Category target = categoryRepository.findById(kanCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not Found: " + kanCode));

        // 2) 리홈 대상(99999999) 존재확인 (Seeder로 생성되어 있어야 함)
        if (!categoryRepository.existsByKanCode(DEFAULT_KAN)) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "기본 카테고리(" + DEFAULT_KAN + ")가 존재하지 않습니다.");
        }

        // 3) 상품리홈 (네이티브 벌크 업데이트로 빠르게)
        productRepository.rehomeProducts(kanCode, DEFAULT_KAN);

        //4) 자식 카테고리 리홈 (parent FK가 있을때만 - Repository 시그니처 기준)
        if (categoryRepository.existsByParent_KanCode(kanCode)) {
            categoryRepository.rehomeChildren(kanCode, DEFAULT_KAN);
        }
        try {
            categoryRepository.delete(target);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "하위에 데이터가 있어 삭제할 수 없습니다.");
        }
    }
}