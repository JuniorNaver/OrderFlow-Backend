package com.youthcase.orderflow.mockTest;

import com.youthcase.orderflow.master.domain.Store;
import com.youthcase.orderflow.master.repository.StoreRepository;
import com.youthcase.orderflow.pr.domain.Product;
import com.youthcase.orderflow.pr.repository.ProductRepository;
import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.repository.STKRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class MockSD {

    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final STKRepository stkRepository;

    @Bean
    CommandLineRunner initMockData() {
        return args -> {

            // ✅ 매장 (없으면 생성)
            if (storeRepository.count() == 0) {
                Store store = Store.builder()
                        .storeId("S001")
                        .storeName("서울 강남점")
                        .address("서울시 강남구 테헤란로 123")
                        .addressDetail("강남역 5번 출구 앞")
                        .postCode("06234")
                        .build();
                storeRepository.save(store);
                System.out.println("✅ 매장 더미 생성: " + store.getStoreName());
            }

            // ✅ 상품 (없으면 생성)
            if (productRepository.count() == 0) {
                Product cola = Product.builder()
                        .productName("콜라 500ml")
                        .gtin("8801234567890")
                        .price(new BigDecimal("1800"))
                        .build();
                productRepository.save(cola);
                System.out.println("✅ 상품 더미 생성: " + cola.getProductName());

                // ✅ 재고 (없으면 생성)
                STK stk = STK.builder()
                        .product(cola)
                        .quantity(100)
                        .status("ACTIVE")
                        .build();
                stkRepository.save(stk);
                System.out.println("✅ 재고 더미 생성: " + cola.getProductName() + " 수량=" + stk.getQuantity());
            }
        };
    }
}