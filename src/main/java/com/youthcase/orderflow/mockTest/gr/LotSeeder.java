package com.youthcase.orderflow.mockTest.gr;

import com.youthcase.orderflow.gr.domain.GoodsReceiptItem;
import com.youthcase.orderflow.gr.domain.Lot;
import com.youthcase.orderflow.gr.repository.GoodsReceiptItemRepository;
import com.youthcase.orderflow.gr.repository.LotRepository;
import com.youthcase.orderflow.gr.status.LotStatus;
import com.youthcase.orderflow.master.product.domain.ExpiryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * 🧾 LotSeeder
 * --------------------------------------------------------
 * - LOT 데이터를 GR_ITEM 기준으로 생성
 * - 각 GR_ITEM당 1~2 LOT 생성 (입고 수량 분할)
 * --------------------------------------------------------
 */
@Slf4j
@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class LotSeeder implements CommandLineRunner {

    private final GoodsReceiptItemRepository grItemRepository;
    private final LotRepository lotRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🧾 [LotSeeder] Start creating LOT records...");

        List<GoodsReceiptItem> grItems = grItemRepository.findAll();
        if (grItems.isEmpty()) {
            log.warn("⚠️ [LotSeeder] No GR_ITEM found — skipping LOT seeding.");
            return;
        }

        int totalLots = 0;

        for (GoodsReceiptItem item : grItems) {
            // 입고 수량을 2개 LOT으로 분할 (예: 12 → 8 + 4)
            long totalQty = item.getQty() != null ? item.getQty() : 0L;
            long firstLotQty = Math.round(totalQty * 0.6);
            long secondLotQty = totalQty - firstLotQty;

            if (totalQty == 0) continue;

            // 첫 번째 LOT
            Lot lot1 = Lot.builder()
                    .product(item.getProduct())
                    .goodsReceiptItem(item)
                    .qty(firstLotQty)
                    .mfgDate(LocalDate.now().minusDays(3))
                    .expDate(LocalDate.now().plusDays(180))
                    .expiryType(ExpiryType.MFG_BASED)
                    .status(LotStatus.ACTIVE)
                    .createdAt(OffsetDateTime.now())
                    .updatedAt(OffsetDateTime.now())
                    .build();
            lotRepository.save(lot1);

            // 두 번째 LOT (if qty > 1)
            if (secondLotQty > 0) {
                Lot lot2 = Lot.builder()
                        .product(item.getProduct())
                        .goodsReceiptItem(item)
                        .qty(secondLotQty)
                        .mfgDate(LocalDate.now().minusDays(2))
                        .expDate(LocalDate.now().plusDays(150))
                        .expiryType(ExpiryType.MFG_BASED)
                        .status(LotStatus.ACTIVE)
                        .createdAt(OffsetDateTime.now())
                        .updatedAt(OffsetDateTime.now())
                        .build();
                lotRepository.save(lot2);
            }

            item.updateQtyFromLots(); // GR_ITEM 수량 갱신
            totalLots += (secondLotQty > 0 ? 2 : 1);
        }

        log.info("✅ [LotSeeder] Created {} LOT records based on {} GR_ITEMs.", totalLots, grItems.size());
    }
}
