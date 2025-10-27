package com.youthcase.orderflow.mockTest.master;

import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.store.domain.StoreType;
import com.youthcase.orderflow.master.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 🏪 StoreSeeder (도메인 필드 매핑 기준)
 * --------------------------------------------------------
 * - STORE_MASTER 도메인 필드명과 일치하도록 수정
 * - address / active / bizHours / contactNumber 반영
 * --------------------------------------------------------
 */
@Slf4j
@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class StoreSeeder implements CommandLineRunner {

    private final StoreRepository storeRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🏪 [StoreSeeder] Seeding STORE_MASTER...");

        if (storeRepository.existsById("S001")) {
            log.warn("⚠️ Store S001 already exists — skipping creation.");
            return;
        }

        Store store = Store.builder()
                .storeId("S001")
                .storeName("OrderFlow 시범점")
                .brandCode("CU")
                .regionCode("11")
                .managerId("HQ001")
                .storeType(StoreType.FRANCHISE)
                .openDate(LocalDate.now().minusYears(1))
                .ownerName("유스케이스 편의점")
                .bizHours("08:00~23:00")
                .contactNumber("02-1234-5678")
                .postCode("04524")
                .address("서울특별시 중구 청계천로 100")
                .addressDetail("1층")
                .active(true)
                .longitude(new BigDecimal("126.9842"))
                .latitude(new BigDecimal("37.5679"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        storeRepository.saveAndFlush(store);
        log.info("✅ [StoreSeeder] Default store created: {}", store.getStoreId());
    }
}
