package com.youthcase.orderflow.mockTest;

import com.youthcase.orderflow.master.price.domain.Price;
import com.youthcase.orderflow.master.price.repository.PriceRepository;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 💰 PriceSeeder
 * - Product 테이블의 GTIN 기준으로 PRICE_MASTER 데이터 자동 생성
 * - 매입가 = 기준가 * 0.8 (예시)
 * - 매출가 = 기준가 (Product.price 그대로)
 */
@Slf4j
@Component
@Profile({"dev", "local"})
@Order(3)
@RequiredArgsConstructor
public class PriceSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void run(String... args) {
        List<Product> products = productRepository.findAll();
        int inserted = 0;

        // PriceSeeder (핵심만)
        for (Product p : products) {
            // 기존 가격 있으면 skip
            if (em.find(Price.class, p.getGtin()) != null) continue;

            Price price = new Price();
            price.setProduct(p); // @MapsId가 GTIN을 가져감
            price.setPurchasePrice(p.getPrice().multiply(new BigDecimal("0.8")));
            price.setSalePrice(p.getPrice());

            em.persist(price);   // <-- merge/save X, persist O
        }


        em.flush();
        em.clear();

        log.info("✅ PriceSeeder done. inserted={}", inserted);
    }
}

