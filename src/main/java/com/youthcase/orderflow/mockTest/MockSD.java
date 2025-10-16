package com.youthcase.orderflow.mockTest;

import com.youthcase.orderflow.master.domain.Store;
import com.youthcase.orderflow.master.repository.StoreRepository;
import com.youthcase.orderflow.pr.domain.*;
import com.youthcase.orderflow.pr.domain.enums.ExpiryType;
import com.youthcase.orderflow.pr.domain.enums.StorageMethod;
import com.youthcase.orderflow.pr.domain.enums.Unit;
import com.youthcase.orderflow.pr.repository.CategoryRepository;
import com.youthcase.orderflow.pr.repository.LotRepository;
import com.youthcase.orderflow.pr.repository.ProductRepository;
import com.youthcase.orderflow.stk.domain.*;
import com.youthcase.orderflow.stk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class MockSD {

    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final STKRepository stkRepository;
    private final CategoryRepository categoryRepository;
    private final WarehouseRepository warehouseRepository; // ✅ 추가
    private final LotRepository lotRepository; // ✅ 추가

    @Bean
    CommandLineRunner initMockData() {
        return args -> {

            // ✅ 1. 매장
            if (storeRepository.count() == 0) {
                Store store = Store.builder()
                        .storeId("S001")
                        .storeName("서울 강남점")
                        .brandCode("CU")
                        .regionCode("SEOUL")
                        .address("서울시 강남구 테헤란로 123")
                        .addressDetail("강남역 5번 출구 앞")
                        .postCode("06234")
                        .active(true)
                        .build();
                storeRepository.save(store);
                System.out.println("✅ 매장 더미 생성: " + store.getStoreName());
            }

            // ✅ 2. 창고 (없으면 생성)
            Warehouse warehouse = warehouseRepository.findById("W001")
                    .orElseGet(() -> {
                        Warehouse w = Warehouse.builder()
                                .warehouseId("W001")
                                .storageCondition("ROOM")
                                .maxCapacity(1000.0)
                                .currentCapacity(100.0)
                                .spotId(1L)
                                .build();
                        warehouseRepository.save(w);
                        System.out.println("✅ 창고 더미 생성: " + w.getWarehouseId());
                        return w;
                    });

            // ✅ 3. 카테고리
            Category drinkCategory = categoryRepository.findByKanCode("DRINK")
                    .orElseGet(() -> {
                        Category c = new Category();
                        c.setKanCode("DRINK");
                        c.setLargeCategory("음료");
                        return categoryRepository.save(c);
                    });

            // ✅ 4. 상품
            Product cola = productRepository.findById("8801234567890")
                    .orElseGet(() -> {
                        Product p = Product.builder()
                                .gtin("8801234567890")
                                .productName("콜라 500ml")
                                .unit(Unit.EA)
                                .price(new BigDecimal("1800"))
                                .storageMethod(StorageMethod.ROOM_TEMP)
                                .category(drinkCategory)
                                .expiryType(ExpiryType.NONE)
                                .orderable(true)
                                .build();
                        productRepository.save(p);
                        System.out.println("✅ 상품 더미 생성: " + p.getProductName());
                        return p;
                    });

            // ✅ 5. LOT (없으면 생성)
            Lot dummyLot = new Lot();
            dummyLot.setExpDate(LocalDate.now().plusMonths(6)); // ✅ 6개월 유통기한
            dummyLot.setProduct(cola);
            dummyLot.setExpiryType(ExpiryType.NONE);
            dummyLot.setQty(new BigDecimal("100"));
            dummyLot.setStatus(Lot.LotStatus.ACTIVE);
            lotRepository.save(dummyLot);
            System.out.println("✅ LOT 더미 생성: ID=" + dummyLot.getLotId());

            // ✅ 6. 재고
            if (stkRepository.count() == 0) {
                STK stk = STK.builder()
                        .product(cola)
                        .warehouse(warehouse) // ✅ 필수
                        .lot(dummyLot)             // ✅ 필수
                        .quantity(100)
                        .status("ACTIVE")
                        .hasExpirationDate(false)
                        .build();
                stkRepository.save(stk);
                System.out.println("✅ 재고 더미 생성: " + cola.getProductName() + " 수량=" + stk.getQuantity());
            }
        };
    }
}
