package com.youthcase.orderflow.mockTest.gr;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.repository.UserRepository;
import com.youthcase.orderflow.gr.domain.GoodsReceiptHeader;
import com.youthcase.orderflow.gr.domain.GoodsReceiptItem;
import com.youthcase.orderflow.gr.repository.GoodsReceiptHeaderRepository;
import com.youthcase.orderflow.gr.repository.GoodsReceiptItemRepository;
import com.youthcase.orderflow.gr.status.GoodsReceiptStatus;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import com.youthcase.orderflow.master.warehouse.repository.WarehouseRepository;
import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.repository.POHeaderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class GoodsReceiptSeeder implements CommandLineRunner {

    private final GoodsReceiptHeaderRepository grHeaderRepository;
    private final GoodsReceiptItemRepository grItemRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final POHeaderRepository poHeaderRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("📦 [GoodsReceiptSeeder] Start creating GR_HEADER and GR_ITEM...");

        // ✅ FK 준비: PO, USER, WAREHOUSE 존재 확인
        POHeader po = poHeaderRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("PO_HEADER 데이터가 필요합니다."));
        User user = userRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("APP_USER 데이터가 필요합니다."));
        Warehouse warehouse = warehouseRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("WAREHOUSE 데이터가 필요합니다."));

        // ✅ GR_HEADER 생성
        GoodsReceiptHeader header = GoodsReceiptHeader.builder()
                .poHeader(po)
                .user(user)
                .status(GoodsReceiptStatus.RECEIVED)
                .receiptDate(LocalDate.now())
                .note("자동 생성된 입고 테스트 데이터")
                .build();
        grHeaderRepository.save(header);

        // ✅ GR_ITEM 생성
        List<Product> products = productRepository.findAll().stream().limit(5).toList();
        for (Product product : products) {
            GoodsReceiptItem item = GoodsReceiptItem.builder()
                    .header(header)
                    .warehouse(warehouse)
                    .product(product)
                    .qty(20L)
                    .note("테스트 입고 아이템")
                    .build();

            grItemRepository.save(item);
            log.info("🧩 GR_ITEM 생성: {} ({})", product.getProductName(), product.getGtin());
        }

        log.info("✅ [GoodsReceiptSeeder] GR_HEADER + GR_ITEM 데이터 생성 완료.");
    }
}
