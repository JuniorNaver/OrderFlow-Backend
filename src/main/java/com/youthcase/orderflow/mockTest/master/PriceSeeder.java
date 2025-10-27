package com.youthcase.orderflow.mockTest.master;

import com.youthcase.orderflow.master.price.domain.Price;
import com.youthcase.orderflow.master.price.repository.PriceRepository;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 💰 PriceSeeder
 * ------------------------------------------------------------
 * - Product(GTIN) 기준으로 PRICE_MASTER 데이터 자동 생성
 * - 매입가(purchasePrice) = 기준가(price) * 0.8
 * - 매출가(salePrice)     = 기준가(price)
 * - 중복 시 skip
 * ------------------------------------------------------------
 */
@Slf4j
@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class PriceSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void run(String... args) {
        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            log.warn("⚠️ No products found. Skip PriceSeeder.");
            return;
        }

        int inserted = 0;

        for (Product product : products) {
            // 이미 가격 데이터가 존재하면 skip
            if (priceRepository.findById(product.getGtin()).isPresent()) {
                log.debug("ℹ️ Skip (already exists): {}", product.getProductName());
                continue;
            }

            // 가격 계산
            BigDecimal base = product.getPrice();
            BigDecimal purchase = base.multiply(BigDecimal.valueOf(0.8))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal sale = base.setScale(2, RoundingMode.HALF_UP);

            // Price 엔티티 생성 (@MapsId -> GTIN 매핑)
            Price price = Price.builder()
                    .product(product)
                    .purchasePrice(purchase)
                    .salePrice(sale)
                    .build();

            em.persist(price);
            inserted++;

            log.info("💰 Price inserted: {} | 매입가={} | 매출가={}",
                    product.getProductName(), purchase, sale);
        }

        em.flush();
        em.clear();

        log.info("✅ PriceSeeder done. inserted={}", inserted);
    }
}
