package com.youthcase.orderflow.mockTest.po;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.repository.UserRepository;
import com.youthcase.orderflow.master.price.repository.PriceRepository;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.domain.POStatus;
import com.youthcase.orderflow.po.repository.POHeaderRepository;
import com.youthcase.orderflow.po.repository.POItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 🧾 POSeeder (다중 상태 버전)
 * --------------------------------------------------------
 * - PR(발주 요청), S(저장), PO(확정) 상태 각각 1건씩 생성
 * - GR(입고) 테스트 및 발주 상태 전이 시나리오용
 * --------------------------------------------------------
 */
@Slf4j
@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class POSeeder {

    private final POHeaderRepository poHeaderRepository;
    private final POItemRepository poItemRepository;
    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;
    private final UserRepository userRepository;

    @Transactional
    public void run(String... args) {
        log.info("🧾 [POSeeder] Creating multiple PO_HEADERs (PR / S / PO)...");

////        // 1️⃣ 사용자 조회
////        User user = userRepository.findAll().stream().findFirst()
////                .orElseThrow(() -> new IllegalStateException("기본 User 데이터가 필요합니다. (AppUserSeeder 확인)"));
////
////        // 2️⃣ 상품 조회
////        List<Product> products = productRepository.findAll();
////        if (products.isEmpty()) {
////            throw new IllegalStateException("Product 데이터가 필요합니다. (ProductSeeder 확인)");
////        }
////
////        // 3️⃣ 생성할 상태 목록
////        POStatus[] statuses = {POStatus.PR, POStatus.S,
////                POStatus.PO, POStatus.PO,
////                POStatus.GI, POStatus.PARTIAL_RECEIVED, POStatus.FULLY_RECEIVED, POStatus.FULLY_RECEIVED,
////                POStatus.CANCELED
////        };
////
////        int headerSeq = 1;
////
////        for (POStatus status : statuses) {
////            String branchCode = user.getStore().getStoreId();
////            String seq = String.format("%02d", headerSeq++);
////            String externalId = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
////                    + branchCode
////                    + seq
////                    + String.format("%03d", (int)(Math.random() * 1000));
////
////
////            POHeader header = POHeader.builder()
////                    .status(status)
////                    .actionDate(LocalDate.now())
////                    .user(user)
////                    .remarks("Auto-generated " + status + " order for test")
////                    .externalId(externalId)
////                    .totalAmount(BigDecimal.ZERO)
////                    .build();
////            poHeaderRepository.save(header);
////
////            // 4️⃣ 아이템 2~3개 생성
////            BigDecimal totalAmount = BigDecimal.ZERO;
////            int itemCount = Math.min(3, products.size());
////            for (int i = 0; i < itemCount; i++) {
////                Product product = products.get(i);
////                BigDecimal price = priceRepository.findPurchasePriceByGtin(product.getGtin())
////                        .orElse(BigDecimal.valueOf(2500));
////
////                long qty = (status == POStatus.PO) ? 20L : 10L; // PO는 좀 더 많게
////                BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(qty));
////
////                POItem item = POItem.builder()
////                        .poHeader(header)
////                        .product(product)
////                        .orderQty(qty)
////                        .pendingQty(qty)
////                        .shippedQty(0L)
////                        .purchasePrice(price)
////                        .expectedArrival(LocalDate.now().plusDays(1))
////                        .status(status)
////                        .build();
////
////                item.calculateTotal();
////                poItemRepository.save(item);
////                totalAmount = totalAmount.add(lineTotal);
////            }
////
////            header.setTotalAmount(totalAmount);
////            poHeaderRepository.save(header);
////
////            log.info("✅ [POSeeder] Created {} header (externalId={}, items={}, total={})",
////                    status, externalId, itemCount, totalAmount);
//        }

        log.info("🎉 [POSeeder] Completed: 3 headers (PR / S / PO) created successfully.");
    }
}
