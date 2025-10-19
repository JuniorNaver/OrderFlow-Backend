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
            String imageUrl, String description,
            Integer shelfLifeDays,
            Integer w, Integer d, Integer h,
            Boolean orderable
    ) {}

    @Override
    @Transactional
    public void run(String... args) {
        // 필요한 만큼만 시작: 실온/라면, 냉장/우유, 냉동/만두
        List<Seed> seeds = List.of(
                new Seed("8809778498260","하림 맛나면 448g (112g x 4봉)", Unit.BOX, "950",   StorageMethod.ROOM_TEMP, "01120401", "https://firebasestorage.googleapis.com/v0/b/steady-copilot-206205.appspot.com/o/goods%2Fbc73af40-c9ad-4d6b-b072-629db1d8592f%2Fbc73af40-c9ad-4d6b-b072-629db1d8592f_front_angle_1000.jpg?alt=media&token=e0ee4e97-d21b-4fdf-a748-d1e2df56671a","봉지라면 4입", 360, 16,12,26, true),
                new Seed("8801073216624","삼양 뿌팟퐁커리 불닭볶음면 큰컵 105g", Unit.EA, "1100",  StorageMethod.ROOM_TEMP, "01120402", "https://firebasestorage.googleapis.com/v0/b/steady-copilot-206205.appspot.com/o/goods%2F419756cd-b202-4162-a1d7-0d555d59a814%2F419756cd-b202-4162-a1d7-0d555d59a814_front_angle_1000.jpg?alt=media&token=41f7182e-865f-4837-afc0-e30c25a010ed","컵라면(대)",360,14,14,9,true ),
                new Seed("8801115115809","서울우유 저지방 우유 200ml", Unit.EA, "2900",  StorageMethod.COLD,"01020101", "https://firebasestorage.googleapis.com/v0/b/steady-copilot-206205.appspot.com/o/goods%2Fd4e91179-7014-4295-be67-a8f4d0d6ff36%2Fd4e91179-7014-4295-be67-a8f4d0d6ff36_front_angle_1000.jpg?alt=media&token=8148e663-1066-43f5-8452-dcf4d19b4434", "저지방우유, 냉장보관",12,6,3,11,true),
                new Seed("8809929360583","레 제주 더 밀크 유기농 A2 플러스 180mL", Unit.EA, "2700",  StorageMethod.COLD, "01020102", "https://firebasestorage.googleapis.com/v0/b/steady-copilot-206205.appspot.com/o/goods%2F44c8a8a9-6da3-4c6a-8ac9-09a9e1c525ed%2F44c8a8a9-6da3-4c6a-8ac9-09a9e1c525ed_front_angle_1000.jpg?alt=media&token=b6d66357-9d7a-43df-9e79-1c2eedcd28fd","직접 농장에서 갓짠 우유, 냉장보관",10,5,5,11,true),
                new Seed("8809456642756","굿프랜즈 푸짐한 한끼 왕교자 1.05kg",Unit.EA, "10900", StorageMethod.FROZEN,"01120301", "https://firebasestorage.googleapis.com/v0/b/steady-copilot-206205.appspot.com/o/goods%2F076374ff-04b3-4f6f-a36e-fb9db8d4ff6e%2F076374ff-04b3-4f6f-a36e-fb9db8d4ff6e_front_angle_1000.jpg?alt=media&token=029d77fb-934d-48d9-bf58-aac25c97bb93", "맛있는만두",21, 26,3,36,true)
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
