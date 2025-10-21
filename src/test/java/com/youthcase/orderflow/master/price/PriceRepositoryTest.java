package com.youthcase.orderflow.master.price;

import com.youthcase.orderflow.master.price.domain.Price;
import com.youthcase.orderflow.master.price.repository.PriceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class PriceRepositoryTest {

    @Autowired
    private PriceRepository priceRepository;

    @Test
    void testFindByGtin() {
        // ✅ 테스트 대상 GTIN
        String gtin = "8809456642756";

        // ✅ DB 조회
        Optional<Price> priceOpt = priceRepository.findByGtin(gtin);

        if (priceOpt.isPresent()) {
            Price price = priceOpt.get();
            System.out.println("✅ 조회 성공:");
            System.out.println("GTIN = " + price.getGtin());
            System.out.println("매입단가 = " + price.getPurchasePrice());
            System.out.println("매출단가 = " + price.getSalePrice());
        } else {
            System.out.println("❌ Price not found for GTIN: " + gtin);
        }
    }
}
