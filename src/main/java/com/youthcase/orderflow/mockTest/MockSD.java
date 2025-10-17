package com.youthcase.orderflow.mockTest;

import com.youthcase.orderflow.master.domain.Product;
import com.youthcase.orderflow.master.domain.Store;
import com.youthcase.orderflow.master.domain.Warehouse;
import com.youthcase.orderflow.master.repository.StoreRepository;
import com.youthcase.orderflow.master.repository.WarehouseRepository;
import com.youthcase.orderflow.pr.domain.*;
import com.youthcase.orderflow.master.domain.ExpiryType;
import com.youthcase.orderflow.master.domain.StorageMethod;
import com.youthcase.orderflow.master.domain.Unit;
import com.youthcase.orderflow.pr.repository.CategoryRepository;
import com.youthcase.orderflow.pr.repository.LotRepository;
import com.youthcase.orderflow.master.repository.ProductRepository;
import com.youthcase.orderflow.stk.domain.*;
import com.youthcase.orderflow.stk.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    private final WarehouseRepository warehouseRepository;
    private final LotRepository lotRepository;

    @PersistenceContext
    private EntityManager em;

    @Bean
    CommandLineRunner initMockData() {
        return args -> {
            System.out.println("🚀 [MOCK DATA INIT] POS SD 모듈용 테스트 데이터 세팅 시작");

            // ✅ 1. 매장 생성
            Store store = storeRepository.findById("S001").orElseGet(() -> {
                Store s = Store.builder()
                        .storeId("S001")
                        .storeName("서울 강남점")
                        .brandCode("CU")
                        .regionCode("SEOUL")
                        .address("서울시 강남구 테헤란로 123")
                        .addressDetail("강남역 5번 출구 앞")
                        .postCode("06234")
                        .active(true)
                        .build();
                storeRepository.saveAndFlush(s);
                System.out.println("✅ 매장 생성 완료: " + s.getStoreName());
                return s;
            });

            // ✅ 2. 창고 (WAREHOUSE_MASTER)
            Warehouse warehouse = warehouseRepository.findById("W001").orElseGet(() -> {
                Warehouse w = Warehouse.builder()
                        .warehouseId("W001")
                        .storageCondition("ROOM")
                        .maxCapacity(1000.0)
                        .currentCapacity(100.0)
                        .store(store)
                        .build();
                warehouseRepository.saveAndFlush(w);
                System.out.println("✅ 창고 생성 완료: " + w.getWarehouseId());
                return w;
            });

            // ✅ 3. 카테고리 (음료)
            Category drinkCategory = categoryRepository.findByKanCode("DRINK").orElseGet(() -> {
                Category c = new Category();
                c.setKanCode("DRINK");
                c.setLargeCategory("음료");
                categoryRepository.saveAndFlush(c);
                System.out.println("✅ 카테고리 생성: " + c.getLargeCategory());
                return c;
            });

            // ✅ 4. 상품 (콜라)
            Product cola = productRepository.findById("8801234567890").orElseGet(() -> {
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
                productRepository.saveAndFlush(p);
                System.out.println("✅ 상품 생성 완료: " + p.getProductName());
                return p;
            });

            // ✅ 5. LOT (유통기한 6개월)
            Lot lot = lotRepository.findAll().stream()
                    .filter(l -> l.getProduct().getGtin().equals("8801234567890"))
                    .findFirst()
                    .orElseGet(() -> {
                        Lot l = new Lot();
                        l.setProduct(cola);
                        l.setExpDate(LocalDate.now().plusMonths(6));
                        l.setExpiryType(ExpiryType.NONE);
                        l.setQty(new BigDecimal("100"));
                        l.setStatus(Lot.LotStatus.ACTIVE);
                        lotRepository.saveAndFlush(l);
                        System.out.println("✅ LOT 생성 완료: LOT_ID = " + l.getLotId());
                        return l;
                    });

            // ✅ 6. 재고 (MM_STOCK)
            if (stkRepository.count() == 0) {
                STK stock = STK.builder()
                        .product(cola)
                        .warehouse(warehouse)
                        .lot(lot)
                        .quantity(100)
                        .status("ACTIVE")
                        .hasExpirationDate(false)
                        .build();
                stkRepository.saveAndFlush(stock);
                System.out.println("✅ 재고 생성 완료: " + cola.getProductName() + " 수량 = " + stock.getQuantity());
            }

            em.clear();
            System.out.println("🎯 [MOCK DATA INIT COMPLETE] 모든 기본 데이터 세팅 완료 ✅");
        };
    }
}
