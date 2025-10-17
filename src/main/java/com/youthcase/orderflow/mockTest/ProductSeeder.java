package com.youthcase.orderflow.mockTest;

import com.youthcase.orderflow.master.product.domain.ExpiryType;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.product.domain.StorageMethod;
import com.youthcase.orderflow.master.product.domain.Unit;
import com.youthcase.orderflow.pr.repository.CategoryRepository;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@Profile({"dev","local"})          // 운영 제외
@Order(2)                          // CategorySeeder가 @Order(1)이라 가정
@RequiredArgsConstructor
public class ProductSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private record Seed(
            String gtin, String name,
            Unit unit, String price,          // BigDecimal은 문자열로 받아서 정확도 보장
            StorageMethod sm, String kanCode,
            String imageUrl, String description
    ) {}

    @Override
    @Transactional
    public void run(String... args) {
        // 필요한 만큼만 시작: 실온/라면, 냉장/우유, 냉동/만두
        List<Seed> seeds = List.of(
                new Seed("8809778498260","하림 맛나면 448g (112g x 4봉)", Unit.BOX, "950",   StorageMethod.ROOM_TEMP, "01120401", null, null),
                new Seed("8801073216624","삼양 뿌팟퐁커리 불닭볶음면 큰컵 105g", Unit.EA, "1100",  StorageMethod.ROOM_TEMP, "01120401", null, null),
                new Seed("8800000000010","서울우유 1L",        Unit.ML, "2900",  StorageMethod.COLD,      "01020101", null, null),
                new Seed("8800000000011","매일우유 900ml",     Unit.ML, "2700",  StorageMethod.COLD,      "01020101", null, null),
                new Seed("8800000000020","비비고 왕교자 1.05kg",Unit.KG, "10900", StorageMethod.FROZEN,    "01120301", null, null)
        );

        int inserted = 0, skipped = 0, missingCat = 0;

        for (Seed s : seeds) {
            if (productRepository.findByGtin(s.gtin()).isPresent()) {
                skipped++; continue;
            }
            var cat = categoryRepository.findByKanCode(s.kanCode()).orElse(null);
            if (cat == null) {                   // KAN 누락 시 건너뛰기
                log.warn("Skip: category not found for KAN={}", s.kanCode());
                missingCat++; continue;
            }

            var p = Product.builder()
                    .gtin(s.gtin())
                    .productName(s.name())
                    .unit(s.unit())                          // Unit nullable이면 생략 가능
                    .price(new BigDecimal(s.price()))        // 엔티티에서 2자리로 정규화됨
                    .storageMethod(s.sm())
                    .category(cat)
                    .imageUrl(s.imageUrl())
                    .description(s.description())
                    .orderable(Boolean.TRUE)
                    .expiryType(ExpiryType.NONE)
                    .build();

            // 선택: 치수/유통기한 예시
            // p.setShelfLifeDays(365);
            // p.setDimensionsMm(120, 40, 180);

            productRepository.save(p);
            inserted++;
        }

        log.info("ProductSeeder done. inserted={}, skipped(exists)={}, missingCategory={}",
                inserted, skipped, missingCat);
    }
}
