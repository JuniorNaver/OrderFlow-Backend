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

import java.util.*;
import java.util.stream.Collectors;

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

        // 2) 상품 존재 시 삭제 금지
        if (productRepository.existsByCategory_KanCode(kanCode)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "해당 카테고리에 상품이 있어 삭제할 수 없습니다.");
        }

        // 3) 자식 카테고리 존재 시 삭제 금지
        if (categoryRepository.existsByParent_KanCode(kanCode)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "하위 카테고리가 있어 삭제할 수 없습니다.");
        }

        // 4) 진짜 삭제
        try {
            categoryRepository.delete(target);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "연관 데이터로 인해 삭제할 수 없습니다.");
        }
    }

    // ✅ 추가: 카테고리별 대표 상품명 Top-N을 배치로 조회
    @Transactional(readOnly = true)
    public Map<String, List<String>> getProductSamplesByCategories(List<String> kanCodes, Integer limit) {
        if (kanCodes == null || kanCodes.isEmpty()) return Collections.emptyMap();
        int top = (limit == null || limit <= 0) ? 2 : Math.min(limit, 10);

        List<ProductRepository.CategoryProductSampleProjection> rows =
                productRepository.findCategoryProductSamples(kanCodes, top);

        return rows.stream().collect(Collectors.groupingBy(
                ProductRepository.CategoryProductSampleProjection::getKanCode,
                LinkedHashMap::new,
                Collectors.mapping(ProductRepository.CategoryProductSampleProjection::getProductName, Collectors.toList())
        ));
    }
}
