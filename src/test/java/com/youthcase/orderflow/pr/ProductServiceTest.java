package com.youthcase.orderflow.pr;

import com.youthcase.orderflow.pr.domain.Product;
import com.youthcase.orderflow.pr.domain.StorageMethod;
import com.youthcase.orderflow.pr.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    public void testCalculateDueDate() {
        // 1. 테스트용 Product 생성
        Product product = new Product();
        product.setProductName("우유");
        product.setStorageMethod(StorageMethod.COLD); // 냉장 1일

        // 2. Service 호출
        LocalDate dueDate = productService.calculateDueDate(product);

        // 3. 결과 확인
        System.out.println("상품: " + product.getProductName());
        System.out.println("보관방법: " + product.getStorageMethod().getDisplayName());
        System.out.println("예상 도착일: " + dueDate);

        // 4. 단위 테스트로 검증 (오늘 + 1일)
        assertEquals(LocalDate.now().plusDays(1), dueDate);
    }
}
