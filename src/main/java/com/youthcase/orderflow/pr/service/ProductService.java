package com.youthcase.orderflow.pr.service;

import com.youthcase.orderflow.pr.domain.Product;
import com.youthcase.orderflow.pr.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 상품 전체 조회
    public List<Product> getAllProducts() {
        return productRepository.findAll();

    }

    // 상품 단건 조회
    public Optional<Product> getProductById(Long gtin) {
        return productRepository.findById(gtin);

    }

    // 상품 등록
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // 상품 수정
    public Product updateProduct(Long gtin, Product productDetails) {
        Product product = productRepository.findById(gtin)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + gtin));

        product.setProductName(productDetails.getProductName());
        product.setUnit(productDetails.getUnit());
        product.setPrice(productDetails.getPrice());
        product.setStorageMethod(productDetails.getStorageMethod());

        return productRepository.save(product);
    }

    // 상품 삭제
    public void deleteProduct(Long gtin) {
        productRepository.deleteById(gtin);
    }
}
