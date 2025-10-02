package com.youthcase.orderflow.pr.controller;

import com.youthcase.orderflow.pr.DTO.ProductRequestDto;
import com.youthcase.orderflow.pr.DTO.ProductUpdateDto;
import com.youthcase.orderflow.pr.domain.Product;
import com.youthcase.orderflow.pr.service.ProductService;
import com.youthcase.orderflow.pr.Mapper.ProductMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 전체 상품 조회
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // 단건 조회
    @GetMapping("/{gtin}")
    public ResponseEntity<Product> getProduct(@PathVariable String gtin) {
        return productService.getProductById(gtin)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 상품 등록
    @PostMapping
    public Product createProduct(@RequestBody @Valid ProductRequestDto dto) {
        Product product = ProductMapper.toEntity(dto, dto.gtin());
        return productService.saveProduct(product);
    }

    // 상품 수정
    @PutMapping("/{gtin}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String gtin,
            @RequestBody @Valid ProductUpdateDto dto) {

        return productService.getProductById(gtin)
                .map(existing -> {
                    ProductMapper.updateEntity(existing, dto); // 기존 엔티티 값 갱신
                    Product updated = productService.updateProduct(gtin, existing);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 상품 삭제
    @DeleteMapping("/{gtin}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String gtin) {
        productService.deleteProduct(gtin);
        return ResponseEntity.noContent().build();
    }
}