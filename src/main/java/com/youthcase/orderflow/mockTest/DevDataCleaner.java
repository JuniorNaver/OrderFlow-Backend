package com.youthcase.orderflow.mockTest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 🧹 DevDataCleaner
 * - dev/local 환경에서 시더 실행 전에 테이블 초기화
 * - 자식 → 부모 순서로 DELETE 수행
 */
@Slf4j
@Component
@Profile({"dev", "local"})
@Order(0)
@RequiredArgsConstructor
public class DevDataCleaner implements CommandLineRunner {

    @PersistenceContext
    private final EntityManager em;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🧹 Cleaning existing data before seeding...");

        // ✅ 자식 → 부모 순서로 삭제
        em.createNativeQuery("DELETE FROM INVENTORY").executeUpdate();
        em.createNativeQuery("DELETE FROM PRICE_MASTER").executeUpdate();
        em.createNativeQuery("DELETE FROM PRODUCT").executeUpdate();
        em.createNativeQuery("DELETE FROM CATEGORY").executeUpdate();

        log.info("✅ DevDataCleaner: INVENTORY, PRICE_MASTER, PRODUCT, CATEGORY cleared.");
    }
}
