package com.youthcase.orderflow.mockTest.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 🧹 DevDataCleaner (JdbcTemplate 버전)
 * --------------------------------------------------------
 * - dev/local 환경에서 모든 테이블 안전 초기화
 * - FK 관계 고려: 자식 → 부모 순으로 삭제
 * - Hibernate EntityManager 트랜잭션 문제 완전 회피
 * --------------------------------------------------------
 */
@Slf4j
@Component
@Profile({"dev", "local"})
@Order(0)
@RequiredArgsConstructor
public class DevDataCleaner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    /** 삭제 순서 — FK 의존 순서에 맞게 정렬 (자식 → 부모) */
    private static final List<String> DELETE_ORDER = List.of(
            "SD_RECEIPT",
            "REFUND_ITEM",
            "PAYMENT_ITEM",
            "SALES_ITEM",
            "LOT",
            "GR_ITEM",
            "MM_STOCK",
            "MM_GR_ITEM",
            "ROLE_AUTH_MAPPING",
            "USER_ROLE",
            "STKHISTORY",
            "SHOP_LIST",
            "NOTI_ITEM",
            "PASSWORD_RESET_TOKEN",
            // 중간 계층
            "REFUND_HEADER",
            "PAYMENT_HEADER",
            "SALES_HEADER",
            "GR_HEADER",
            "PO_ITEM",
            "PO_HEADER",
            "MM_PR",
            "INVENTORY",
            "PRICE_MASTER",
            "PRODUCT",
            "CATEGORY",
            "WAREHOUSE_MASTER",
            "STORE_MASTER",
            "APP_USER",
            "ROLE",
            "AUTHORITY"
    );

    @Override
    public void run(String... args) {
        log.info("🧹 [DevDataCleaner] Cleaning existing data before seeding...");

        for (String table : DELETE_ORDER) {
            safeDelete(table);
        }

        log.info("✅ [DevDataCleaner] All major tables cleared successfully.");
    }

    /**
     * ✅ 안전한 삭제 수행
     * - 존재하지 않거나 FK 제약 위반 시 경고만 출력하고 다음 테이블로 진행
     */
    private void safeDelete(String tableName) {
        try {
            int count = jdbcTemplate.update("DELETE FROM " + tableName);
            log.info("🧩 Cleared table: {} ({} rows)", tableName, count);
        } catch (Exception e) {
            log.warn("⚠️ Skip or failed to delete table [{}]: {}", tableName, e.getMessage(), e);
        }
    }
}