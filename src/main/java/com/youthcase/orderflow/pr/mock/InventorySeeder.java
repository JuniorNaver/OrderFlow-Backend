package com.youthcase.orderflow.pr.mock;

import com.youthcase.orderflow.pr.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile({"dev", "local"})
@Order(3)
@RequiredArgsConstructor
public class InventorySeeder implements CommandLineRunner {

    private final InventoryService inventoryService;

    @Override
    @Transactional
    public void run(String... args) {
        // GTIN은 ProductSeeder에 있는 것과 맞춰서
        inventoryService.receive("8809778498260", 30); // 하림 맛나면
        inventoryService.receive("8801073216624", 25); // 삼양 큰컵
        inventoryService.receive("8800000000010", 50); // 서울우유
        inventoryService.receive("8800000000011", 40); // 매일우유
        inventoryService.receive("8800000000020", 15); // 비비고 왕교자

        log.info("InventorySeeder done.");
    }
}
