package com.youthcase.orderflow.mockTest;

import com.youthcase.orderflow.master.product.domain.*;
import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.store.domain.StoreType;
import com.youthcase.orderflow.master.store.repository.StoreRepository;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import com.youthcase.orderflow.master.warehouse.repository.WarehouseRepository;
import com.youthcase.orderflow.pr.domain.Category;
import com.youthcase.orderflow.pr.domain.Lot;
import com.youthcase.orderflow.pr.repository.CategoryRepository;
import com.youthcase.orderflow.pr.repository.LotRepository;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.repository.STKRepository;
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
            System.out.println("🚀 [MOCK DATA INIT] POS·STOCK 테스트 데이터 세팅 시작");

            // ────────────────────────────────
            // ✅ 1. 점포 생성
            // ────────────────────────────────
            Store store = storeRepository.findById("S001").orElseGet(() -> {
                Store s = Store.builder()
                        .storeId("S001")
                        .storeName("서울 강남점")
                        .brandCode("CU")
                        .regionCode("SEOUL")
                        .managerId("admin01")
                        .storeType(StoreType.DIRECT)
                        .address("서울특별시 강남구 테헤란로 123")
                        .addressDetail("강남역 5번 출구 앞")
                        .postCode("06234")
                        .ownerName("홍길동")
                        .bizHours("08:00~23:00")
                        .contactNumber("02-3456-7890")
                        .active(true)
                        .longitude(new BigDecimal("127.028000"))
                        .latitude(new BigDecimal("37.498000"))
                        .build();
                storeRepository.saveAndFlush(s);
                System.out.println("✅ 점포 생성 완료: " + s.getStoreName());
                return s;
            });

            // ────────────────────────────────
            // ✅ 2. 창고 생성 (3종류)
            // ────────────────────────────────
            createWarehouseIfNotExists("W001", "실온 창고", StorageMethod.ROOM_TEMP, 1000.0, 100.0, store);
            createWarehouseIfNotExists("W002", "냉장 창고", StorageMethod.COLD, 800.0, 50.0, store);
            createWarehouseIfNotExists("W003", "냉동 창고", StorageMethod.FROZEN, 600.0, 30.0, store);

            // ────────────────────────────────
            // ✅ 3. 카테고리 (음료)
            // ────────────────────────────────
            Category drinkCategory = categoryRepository.findByKanCode("DRINK").orElseGet(() -> {
                Category c = new Category();
                c.setKanCode("DRINK");
                c.setLargeCategory("음료");
                c.setMediumCategory("탄산음료");
                c.setSmallCategory("콜라/사이다");
                categoryRepository.saveAndFlush(c);
                System.out.println("✅ 카테고리 생성: " + c.getLargeCategory());
                return c;
            });

            // ────────────────────────────────
            // ✅ 4. 상품 (콜라)
            // ────────────────────────────────
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

            // ────────────────────────────────
            // ✅ 5. LOT (유통기한 6개월)
            // ────────────────────────────────
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

            // ────────────────────────────────
            // ✅ 6. 재고 (MM_STOCK)
            // ────────────────────────────────
            if (stkRepository.count() == 0) {
                Warehouse roomWarehouse = warehouseRepository.findById("W001").orElseThrow();
                STK stock = STK.builder()
                        .product(cola)
                        .warehouse(roomWarehouse)
                        .lot(lot)
                        .quantity(100)
                        .status("ACTIVE")
                        .hasExpirationDate(false)
                        .build();
                stkRepository.saveAndFlush(stock);
                System.out.println("✅ 재고 생성 완료: " + cola.getProductName() + " 수량 = " + stock.getQuantity());
            }

            em.clear();
            System.out.println("🎯 [MOCK DATA INIT COMPLETE] 기본 데이터 세팅 완료 ✅");
        };
    }

    // ────────────────────────────────
    // 🔹 헬퍼: 창고 없으면 생성
    // ────────────────────────────────
    private void createWarehouseIfNotExists(String id, String name, StorageMethod method,
                                            Double maxCap, Double currCap, Store store) {
        warehouseRepository.findById(id).orElseGet(() -> {
            Warehouse w = Warehouse.builder()
                    .warehouseId(id)
                    .warehouseName(name)
                    .storageMethod(method)
                    .maxCapacity(maxCap)
                    .currentCapacity(currCap)
                    .store(store)
                    .build();
            warehouseRepository.saveAndFlush(w);
            System.out.println("✅ 창고 생성 완료: " + name + " (" + method + ")");
            return w;
        });
    }
}
