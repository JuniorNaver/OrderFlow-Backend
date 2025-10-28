package com.youthcase.orderflow.mockTest.gr;

import com.youthcase.orderflow.gr.domain.GoodsReceiptItem;
import com.youthcase.orderflow.gr.domain.Lot;
import com.youthcase.orderflow.gr.repository.GoodsReceiptItemRepository;
import com.youthcase.orderflow.gr.repository.LotRepository;
import com.youthcase.orderflow.gr.status.LotStatus;
import com.youthcase.orderflow.master.product.domain.ExpiryType;
import com.youthcase.orderflow.master.product.domain.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * 🧾 LotSeeder
 * --------------------------------------------------------
 * - GR_ITEM 기준으로 LOT 데이터를 생성
 * - Product의 ExpiryType(USE_BY / BEST_BEFORE / NONE)에 따라 expDate 계산
 * - Product.shelfLifeDays가 존재하면 그것을 우선 적용
 * --------------------------------------------------------
 */
@Slf4j
@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class LotSeeder {

    private final GoodsReceiptItemRepository grItemRepository;
    private final LotRepository lotRepository;

    @Transactional
    public void run(String... args) {
        log.info("🧾 [LotSeeder] Start creating LOT records (using shelfLifeDays + ExpiryType)...");

        List<GoodsReceiptItem> grItems = grItemRepository.findAll();
        if (grItems.isEmpty()) {
            log.warn("⚠️ [LotSeeder] No GR_ITEM found — skipping LOT seeding.");
            return;
        }

        int totalLots = 0;

        for (GoodsReceiptItem item : grItems) {
            Product product = item.getProduct();
            ExpiryType expiryType = product.getExpiryType();
            long totalQty = item.getQty() != null ? item.getQty() : 0L;
            if (totalQty <= 0) continue;

            long firstLotQty = Math.round(totalQty * 0.6);
            long secondLotQty = totalQty - firstLotQty;

            // ✅ LOT1
            Lot lot1 = buildLot(product, item, firstLotQty, expiryType, 3);
            lotRepository.save(lot1);

            // ✅ LOT2
            if (secondLotQty > 0) {
                Lot lot2 = buildLot(product, item, secondLotQty, expiryType, 2);
                lotRepository.save(lot2);
            }

            item.updateQtyFromLots();
            totalLots += (secondLotQty > 0 ? 2 : 1);
        }

        log.info("✅ [LotSeeder] Created {} LOT records based on {} GR_ITEMs.", totalLots, grItems.size());
    }

    /**
     * 🔧 LOT 생성 헬퍼 (ExpiryType + shelfLifeDays 반영)
     */
    private Lot buildLot(Product product, GoodsReceiptItem item, long qty, ExpiryType expiryType, int mfgOffsetDays) {
        LocalDate mfgDate = LocalDate.now().minusDays(mfgOffsetDays);
        LocalDate expDate = calculateExpDate(product, mfgDate);

        return Lot.builder()
                .product(product)
                .goodsReceiptItem(item)
                .qty(qty)
                .mfgDate(mfgDate)
                .expDate(expDate)
                .expiryType(expiryType)
                .status(LotStatus.ACTIVE)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    /**
     * 📆 유통기한 계산 (shelfLifeDays + ExpiryType 기준)
     */
    private LocalDate calculateExpDate(Product product, LocalDate mfgDate) {
        ExpiryType type = product.getExpiryType();
        Integer shelfLife = product.getShelfLifeDays();

        // 1️⃣ 유통기한 없음
        if (type == ExpiryType.NONE) return null;

        // 2️⃣ shelfLifeDays 지정된 경우 → 그것을 우선 사용
        if (shelfLife != null && shelfLife > 0) {
            return mfgDate.plusDays(shelfLife);
        }

        // 3️⃣ 기본값 (유형별 fallback)
        return switch (type) {
            case USE_BY -> mfgDate.plusDays(60);       // 소비기한: 60일
            case BEST_BEFORE -> mfgDate.plusDays(180); // 품질유지기한: 180일
            case NONE -> null;
        };
    }
}
