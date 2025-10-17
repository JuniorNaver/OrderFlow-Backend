package com.youthcase.orderflow.master.service;

import com.youthcase.orderflow.pr.domain.Category;
import com.youthcase.orderflow.master.domain.ExpiryType;
import com.youthcase.orderflow.master.domain.Product;
import com.youthcase.orderflow.master.dto.ProductCreateDto;
import com.youthcase.orderflow.master.dto.ProductResponseDto;
import com.youthcase.orderflow.master.dto.ProductUpdateDto;
import com.youthcase.orderflow.master.mapper.ProductMapper;
import com.youthcase.orderflow.pr.repository.CategoryRepository;
import com.youthcase.orderflow.master.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final Clock clock; // 선택: 테스트/타임존 제어용. 불편하면 제거하고 LocalDate.now() 그대로 써도 OK.

    /* ========= 목록/검색 ========= */
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CLERK')")
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> listAdvanced(String name, String gtin, String categoryCode, Pageable p) {
        if (name != null && !name.isBlank()) {
            return productRepository.findByProductNameContainingIgnoreCase(name, p)
                    .map(ProductMapper::toResp);
        }
        if (gtin != null && !gtin.isBlank()) {
            return productRepository.findByGtinContaining(gtin, p)
                    .map(ProductMapper::toResp);
        }
        if (categoryCode != null && !categoryCode.isBlank()) {
            return productRepository.findByCategory_KanCode(categoryCode, p)
                    .map(ProductMapper::toResp);
        }
        return productRepository.findAll(p).map(ProductMapper::toResp);
    }

    /* ========= 단건 조회 ========= */
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CLERK')")
    @Transactional(readOnly = true)
    public ProductResponseDto get(String gtin) {
        return productRepository.findById(gtin)
                .map(ProductMapper::toResp)
                .orElseThrow(() -> new NotFoundException("Product not found: " + gtin));
    }


    /* ========= 생성 ========= */
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDto create(ProductCreateDto dto) {
        if (productRepository.existsById(dto.gtin())) {
            throw new ConflictException("GTIN already exists: " + dto.gtin());
        }
        Category category = categoryRepository.findByKanCode(dto.categoryCode())
                .orElseThrow(() -> new NotFoundException("Category not found: " + dto.categoryCode()));

        // 금액 스케일(12,2) 고정
       ProductCreateDto normalized = new ProductCreateDto(
                dto.gtin(),
                dto.productName(),
                dto.unit(),
                dto.price().setScale(2, RoundingMode.HALF_UP),
                dto.storageMethod(),
                dto.categoryCode(),
               dto.expiryType() == null ? ExpiryType.NONE : dto.expiryType(), // ← 기본값 보정
               dto.shelfLifeDays()
        );

        Product saved = productRepository.save(ProductMapper.toEntity(normalized, category));
        return ProductMapper.toResp(saved);
    }

    /* ========= 수정 ========= */
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDto update(String gtin, ProductUpdateDto dto) {
        Product entity = productRepository.findById(gtin)
                .orElseThrow(() -> new NotFoundException("Product not found: " + gtin));

        Category category = categoryRepository.findByKanCode(dto.categoryCode())
                .orElseThrow(() -> new NotFoundException("Category not found: " + dto.categoryCode()));

        // 금액 스케일 고정(레코드 재구성 없이 값만 보정)
        ProductUpdateDto normalized = new ProductUpdateDto(
                dto.productName(),
                dto.unit(),
                dto.price().setScale(2, RoundingMode.HALF_UP),
                dto.storageMethod(),
                dto.categoryCode(),
                dto.expiryType(),
                dto.shelfLifeDays()
        );

        ProductMapper.updateEntity(entity, normalized, category); // 더티체킹으로 UPDATE
        return ProductMapper.toResp(entity);
    }

    /* ========= 삭제 ========= */
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String gtin) {
        if (!productRepository.existsById(gtin)) return; // 멱등 삭제
        productRepository.deleteById(gtin);
    }

    /* ========= 유틸 ========= */
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CLERK')")
    public LocalDate calculateDueDate(Product product) {
        if (product.getStorageMethod() == null) {
            throw new IllegalArgumentException("상품의 보관 방식이 지정되지 않았습니다.");
        }
        int days = Math.max(0, product.getStorageMethod().getLeadTimeDays());
        return LocalDate.now(clock).plusDays(days);
    }
}