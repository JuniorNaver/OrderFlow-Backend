package com.youthcase.orderflow.pr.service;

import com.youthcase.orderflow.pr.domain.Product;
import com.youthcase.orderflow.pr.domain.StorageMethod;
import com.youthcase.orderflow.pr.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;


    public boolean checkGtinExists(String gtin) {
        return productRepository.existsByGtin(gtin);
    }

    public Optional<Product> getProductByGtin(String gtin) {
        return productRepository.findByGtin(gtin);
    }

    // 상품 전체 조회
    public List<Product> getAllProducts() {
        return productRepository.findAll();

    }

    // 상품 단건 조회
    public Optional<Product> getProductById(String gtin) {
        return productRepository.findById(gtin);
    }

    public LocalDate calculateDueDate(Product product) {
        if (product.getStorageMethod() == null) {
            throw new IllegalArgumentException("상품의 보관 방식이 지정되지 않았습니다.");
        }
        int leadTime = product.getStorageMethod().getLeadTimeDays();
        if (leadTime < 0) {
            leadTime = 0;
        }
        return LocalDate.now().plusDays(leadTime);
    }

    // 상품 등록
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // 상품 수정
    public Product updateProduct(String gtin, Product updatedProduct) {
        return productRepository.findById(gtin)
                .map(existing -> {
                    existing.setProductName(updatedProduct.getProductName());
                    existing.setUnit(updatedProduct.getUnit());
                    existing.setPrice(updatedProduct.getPrice());
                    existing.setStorageMethod(updatedProduct.getStorageMethod());
                    return productRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Product not found: " + gtin));
    }

    // 상품 삭제
    public void deleteProduct(String gtin) {
        productRepository.deleteById(gtin);
    }
}
