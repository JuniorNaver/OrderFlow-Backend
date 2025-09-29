package com.youthcase.orderflow.pr.controller;

import com.youthcase.orderflow.pr.domain.Product;
import com.youthcase.orderflow.pr.service.ProductService;
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
    public ResponseEntity<Product> getProduct(@PathVariable Long gtin) {
        return productService.getProductById(gtin)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 상품 등록
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    // 상품 수정
    @PutMapping("/{gtin}")
    public Product updateProduct(@PathVariable Long gtin, @RequestBody Product product) {
        return productService.updateProduct(gtin, product);
    }

    // 상품 삭제
    @DeleteMapping("/{gtin}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long gtin) {
        productService.deleteProduct(gtin);
        return ResponseEntity.noContent().build();
    }
}
