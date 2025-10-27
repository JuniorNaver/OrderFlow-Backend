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
import java.util.Optional;

/**
 * 🏪 StoreSeeder (업데이트 버전)
 * --------------------------------------------------------
 * - S001 점포는 항상 존재해야 함
 * - 이미 존재하면 필드 업데이트, 없으면 새로 생성
 * --------------------------------------------------------
 */
@Slf4j
@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class StoreSeeder {

    private final StoreRepository storeRepository;

    @Transactional
    public void run(String... args) {
        final String TARGET_STORE_ID = "S001";

        log.info("🏪 [StoreSeeder] Ensuring STORE_MASTER record for {}...", TARGET_STORE_ID);

        Optional<Store> existingOpt = storeRepository.findById(TARGET_STORE_ID);
        Store store;

        if (existingOpt.isPresent()) {
            // ✅ 이미 존재하면 업데이트
            store = existingOpt.get();
            log.info("🔄 Updating existing store {}", TARGET_STORE_ID);

            store.setStoreName("OrderFlow 시범점");
            store.setBrandCode("CU");
            store.setRegionCode("11");
            store.setManagerId("HQ001");
            store.setStoreType(StoreType.FRANCHISE);
            store.setOpenDate(LocalDate.now().minusYears(1));
            store.setOwnerName("유스케이스 편의점");
            store.setBizHours("08:00~23:00");
            store.setContactNumber("02-1234-5678");
            store.setPostCode("04524");
            store.setAddress("서울특별시 중구 청계천로 100");
            store.setAddressDetail("1층");
            store.setActive(true);
            store.setLongitude(new BigDecimal("126.9842"));
            store.setLatitude(new BigDecimal("37.5679"));
            store.setUpdatedAt(LocalDateTime.now());

        } else {
            // ✅ 없으면 새로 생성
            log.info("🆕 Creating new store {}", TARGET_STORE_ID);

            store = Store.builder()
                    .storeId(TARGET_STORE_ID)
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
        }

        // ✅ 저장 및 즉시 반영
        storeRepository.saveAndFlush(store);
        log.info("✅ [StoreSeeder] Store {} ensured successfully (upsert).", TARGET_STORE_ID);
    }
}
