package com.youthcase.orderflow.mockTest.sd;

import com.youthcase.orderflow.gr.domain.Lot;
import com.youthcase.orderflow.gr.status.LotStatus;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.product.domain.StorageMethod;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import com.youthcase.orderflow.master.warehouse.repository.WarehouseRepository;
import com.youthcase.orderflow.gr.repository.LotRepository;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.domain.StockStatus;
import com.youthcase.orderflow.stk.repository.STKRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;

@Configuration
@Profile("mock")
@RequiredArgsConstructor
public class MockSD {

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final STKRepository stkRepository;
    private final LotRepository lotRepository;

    @Bean
    CommandLineRunner mockDataSeed() {
        return args -> {
            System.out.println("🧩 [MockSD] POS/재고 테스트용 데이터 생성 시작");

            Product cola = productRepository.findById("8801234567890").orElseThrow();
            Warehouse room = warehouseRepository.findByStorageMethod(StorageMethod.ROOM_TEMP).stream().findFirst().orElseThrow();

            Lot lot = Lot.builder()
                    .product(cola)
                    .qty(100L)
                    .expDate(LocalDate.now().plusMonths(6))
                    .status(LotStatus.ACTIVE)
                    .build();
            lotRepository.saveAndFlush(lot);

            STK stk = STK.builder()
                    .product(cola)
                    .warehouse(room)
                    .lot(lot)
                    .quantity(100L)
                    .status(StockStatus.ACTIVE)
                    .hasExpirationDate(false)
                    .build();
            stkRepository.saveAndFlush(stk);

            System.out.println("✅ [MockSD] 테스트 재고 생성 완료");
        };
    }
}
