package com.youthcase.orderflow.mockTest.init;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 🧹 DevDataCleaner (확장 버전)
 * --------------------------------------------------------
 * - dev/local 환경에서 모든 시더 실행 전 전체 테이블 초기화
 * - FK 관계를 고려한 자식 → 부모 순서
 * - AUTH/MASTER/비즈니스 데이터 순서별 정리
 * --------------------------------------------------------
 */
@Slf4j
@Component
@Profile({"dev", "local"})
@Order(0)
@RequiredArgsConstructor
public class DevDataCleaner implements CommandLineRunner {

    @PersistenceContext
    private final EntityManager em;

    private static final List<String> DELETE_ORDER = List.of(
            "SD_RECEIPT_ITEM",
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
            "PR_HEADER",
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
     * 안전한 삭제 수행 (존재하지 않아도 예외 없이 로그만 출력)
     */
    private void safeDelete(String tableName) {
        try {
            int count = em.createNativeQuery("DELETE FROM " + tableName).executeUpdate();
            log.info("🧩 Cleared table: {} ({} rows)", tableName, count);
        } catch (Exception e) {
            log.warn("⚠️ Skip or failed to delete table [{}]: {}", tableName, e.getMessage());
        }
    }
}
