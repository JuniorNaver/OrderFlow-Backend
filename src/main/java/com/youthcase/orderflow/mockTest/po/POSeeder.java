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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class POSeeder implements CommandLineRunner {

    private final POHeaderRepository poHeaderRepository;
    private final POItemRepository poItemRepository;
    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) {
        long t0 = System.currentTimeMillis();
        log.info("🧾 [POSeeder] Start generating PO_HEADER & PO_ITEM data (mixed version)...");

        User user = userRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("기본 User 데이터가 필요합니다. (AppUserSeeder 확인)"));

        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new IllegalStateException("Product 데이터가 필요합니다.");
        }
        log.debug("🔎 Product size={}, sample={}", products.size(),
                products.size() > 0 ? products.get(0).getGtin() : "N/A");

        final int fullyCount = 100;     // FULLY_RECEIVED 수
        final int extraPerStatus = 5;   // 나머지 상태별 추가 수
        int headerSeq = 1;

        // 1) FULLY_RECEIVED 대량 생성
        for (int i = 0; i < fullyCount; i++) {
            try {
                createRandomPO(user, products, headerSeq++, POStatus.FULLY_RECEIVED);
            } catch (Exception e) {
                log.error("❌ [POSeeder] FULLY_RECEIVED 생성 실패: idx={}, seq={}, msg={}",
                        i, headerSeq - 1, e.getMessage(), e);
                // 실패 원인 파악 위해 계속 진행
            }
            // 진행률: 10건 단위로 출력 (i=0만 찍히는 문제 방지)
            if ((i + 1) % 10 == 0 || i == 0) {
                log.info("📦 Progress(FR): {}/{}", i + 1, fullyCount);
            }
        }

        // 2) 다른 상태 몇 개씩 추가
        POStatus[] extraStatuses = {
                POStatus.PR, POStatus.S, POStatus.PO,
                POStatus.GI, POStatus.PARTIAL_RECEIVED, POStatus.CANCELED
        };

        for (POStatus status : extraStatuses) {
            for (int i = 0; i < extraPerStatus; i++) {
                try {
                    createRandomPO(user, products, headerSeq++, status);
                } catch (Exception e) {
                    log.error("❌ [POSeeder] {} 생성 실패: idx={}, seq={}, msg={}",
                            status, i, headerSeq - 1, e.getMessage(), e);
                }
            }
            log.info("➕ Added {} PO(s) for status={}", extraPerStatus, status);
        }

        log.info("🎉 [POSeeder] Completed — {} FULLY_RECEIVED + {} (others) created successfully. elapsed={}ms",
                fullyCount, extraStatuses.length * extraPerStatus, System.currentTimeMillis() - t0);
    }

    private void createRandomPO(User user, List<Product> products, int headerSeq, POStatus status) {
        long tHeader0 = System.currentTimeMillis();

        String branchCode = user.getStore().getStoreId();
        String seq = String.format("%04d", headerSeq);
        String externalId = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + branchCode + seq + String.format("%03d", ThreadLocalRandom.current().nextInt(1000));

        log.debug("🧱 Creating PO header: status={}, seq={}, externalId={}", status, seq, externalId);

        POHeader header = POHeader.builder()
                .status(status)
                .actionDate(LocalDate.now().minusDays(ThreadLocalRandom.current().nextInt(0, 10)))
                .user(user)
                .remarks("Auto-generated " + status + " order (" + seq + ")")
                .externalId(externalId)
                .totalAmount(BigDecimal.ZERO)
                .build();

        poHeaderRepository.save(header);
        log.debug("✅ Saved header: poId={}, status={}, externalId={}, took={}ms",
                header.getPoId(), status, externalId, System.currentTimeMillis() - tHeader0);

        BigDecimal totalAmount = BigDecimal.ZERO;
        int itemCount = (status == POStatus.FULLY_RECEIVED)
                ? ThreadLocalRandom.current().nextInt(3, 7)
                : ThreadLocalRandom.current().nextInt(2, 5);

        for (int j = 0; j < itemCount; j++) {
            long tItem0 = System.currentTimeMillis();

            Product product = products.get(ThreadLocalRandom.current().nextInt(products.size()));
            String gtin = product.getGtin();

            BigDecimal price = priceRepository.findPurchasePriceByGtin(gtin)
                    .orElse(null);
            if (price == null) {
                // 가격 없으면 랜덤 가격으로 보정하고 경고 로그 남김
                price = BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(1500, 4000));
                log.warn("⚠️ Price not found for GTIN={}, fallback price={}", gtin, price);
            }

            long qty = switch (status) {
                case PR -> ThreadLocalRandom.current().nextLong(5, 15);
                case S, PO -> ThreadLocalRandom.current().nextLong(10, 25);
                case GI -> ThreadLocalRandom.current().nextLong(20, 50);
                case PARTIAL_RECEIVED -> ThreadLocalRandom.current().nextLong(15, 40);
                case FULLY_RECEIVED -> ThreadLocalRandom.current().nextLong(30, 100);
                case CANCELED -> ThreadLocalRandom.current().nextLong(5, 10);
                default -> 10L;
            };

            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(qty));

            POItem item = POItem.builder()
                    .poHeader(header)
                    .product(product)
                    .orderQty(qty)
                    .pendingQty((status == POStatus.FULLY_RECEIVED) ? 0L : qty)
                    .shippedQty((status == POStatus.FULLY_RECEIVED) ? qty : 0L)
                    .purchasePrice(price)
                    .expectedArrival(LocalDate.now().plusDays(1))
                    .status(status)
                    .build();

            try {
                item.calculateTotal();
                poItemRepository.save(item);
                totalAmount = totalAmount.add(lineTotal);
                log.debug("   └─ ✅ Saved item: poId={}, gtin={}, qty={}, price={}, status={}, took={}ms",
                        header.getPoId(), gtin, qty, price, status, System.currentTimeMillis() - tItem0);
            } catch (Exception e) {
                log.error("   └─ ❌ Item save failed: poId={}, gtin={}, qty={}, status={}, msg={}",
                        header.getPoId(), gtin, qty, status, e.getMessage(), e);
                // 아이템에서만 실패해도 다음 아이템 계속 진행
            }
        }

        header.setTotalAmount(totalAmount);
        poHeaderRepository.save(header);
        log.debug("💾 Updated header total: poId={}, items={}, totalAmount={}, took={}ms",
                header.getPoId(), itemCount, totalAmount, System.currentTimeMillis() - tHeader0);
    }
}
