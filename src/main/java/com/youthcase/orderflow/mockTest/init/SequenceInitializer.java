package com.youthcase.orderflow.mockTest.init;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 🧩 SequenceInitializer
 * - Oracle USER_SEQUENCES 조회 후 존재하지 않으면 자동 생성
 * - Spring Boot dev/local 환경 전용
 * - Hibernate 실행 전후 구동에도 안전 (트랜잭션 완전 적용)
 */
@Slf4j
@Component
@Profile({"dev", "local"})
@Order(1)
public class SequenceInitializer implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🔧 Checking required Oracle sequences...");
        createSequenceIfNotExists("WAREHOUSE_SEQ");
        createSequenceIfNotExists("STORE_SEQ");
        log.info("✅ Sequence initialization completed.");
    }

    private void createSequenceIfNotExists(String seqName) {
        Long count = ((Number) entityManager.createNativeQuery(
                        "SELECT COUNT(*) FROM USER_SEQUENCES WHERE SEQUENCE_NAME = :name")
                .setParameter("name", seqName)
                .getSingleResult()).longValue();

        if (count == 0) {
            entityManager.createNativeQuery(
                    "CREATE SEQUENCE " + seqName + " START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE"
            ).executeUpdate();
            log.info("✅ Created new sequence: {}", seqName);
        } else {
            log.debug("ℹ️ Sequence already exists: {}", seqName);
        }
    }
}
