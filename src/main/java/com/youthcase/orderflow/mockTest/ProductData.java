package com.youthcase.orderflow.mockTest;

import com.youthcase.orderflow.master.product.domain.ExpiryType;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.product.domain.StorageMethod;
import com.youthcase.orderflow.master.product.domain.Unit;
import com.youthcase.orderflow.pr.repository.CategoryRepository;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class ProductData {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    /** 부팅 시 한 번만 시드 */
    @PostConstruct
    @Transactional
    public void seed() {
        // GTIN, 이름, 단위, 가격, 보관, KAN
        List<Seed> seeds = List.of(
                new Seed("8800000000001","진라면 매운맛(봉지)", Unit.EA, "950",   StorageMethod.ROOM_TEMP, "01120401"), // 실온/라면/봉지라면
                new Seed("8800000000002","신라면(봉지)",       Unit.BOX, "1100",  StorageMethod.ROOM_TEMP, "01120401"),
                new Seed("8800000000010","서울우유 1000ml",        Unit.ML, "2900",  StorageMethod.COLD,      "01020101"), // 냉장/유제품/우유/일반우유
                new Seed("8800000000011","매일우유 900ml",     Unit.ML, "2700",  StorageMethod.COLD,      "01020101"),
                new Seed("8800000000020","비비고 왕교자 1.05kg",Unit.EA, "10900", StorageMethod.FROZEN,    "01120301")  // 냉동/냉동만두
        );

        int inserted = 0;
        for (Seed s : seeds) {
            if (productRepository.findByGtin(s.gtin()).isPresent()) {
                log.debug("skip (exists): {}", s.gtin());
                continue;
            }
            var cat = categoryRepository.findByKanCode(s.kan()).orElse(null);
            if (cat == null) {
                log.warn("skip (category not found): {} - {}", s.gtin(), s.kan());
                continue;
            }

            var product = Product.builder()
                    .gtin(s.gtin())
                    .productName(s.name())
                    .unit(s.unit()) // null 허용이면 생략 가능
                    .price(new BigDecimal(s.price())) // setPrice에서 scale 정규화
                    .storageMethod(s.sm())
                    .category(cat)
                    .imageUrl(null)
                    .description(null)
                    .orderable(Boolean.TRUE)
                    .expiryType(ExpiryType.NONE)
                    .build();

            productRepository.save(product);
            inserted++;
        }
        log.info("DevProductSeeder done. inserted={}", inserted);
    }

    private record Seed(
            String gtin, String name, Unit unit, String price, StorageMethod sm, String kan
    ){}
}
