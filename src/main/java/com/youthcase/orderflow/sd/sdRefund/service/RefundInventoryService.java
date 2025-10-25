package com.youthcase.orderflow.sd.sdRefund.service;

import com.youthcase.orderflow.gr.domain.Lot;
import com.youthcase.orderflow.gr.repository.LotRepository;
import com.youthcase.orderflow.gr.status.LotStatus;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.repository.STKRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundInventoryService {

    private final STKRepository stkRepository;
    private final LotRepository lotRepository;

    /**
     * ♻️ 환불 시 재고 복원
     */
    @Transactional
    public void restoreStock(RefundHeader header) {
        try {
            if (header.getPaymentHeader() == null
                    || header.getPaymentHeader().getSalesHeader() == null) {
                log.warn("⚠️ 환불 헤더에 SalesHeader가 없습니다. 재고 복원 스킵");
                return;
            }

            header.getPaymentHeader()
                    .getSalesHeader()
                    .getSalesItems()
                    .forEach(this::restoreStockForItem);

            log.info("🧾 [헤더 단위 재고 복원 완료] refundId={}", header.getRefundId());

        } catch (Exception e) {
            log.error("❌ 재고 복원 중 오류: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 🔹 개별 SalesItem 단위 재고 복원
     */
    private void restoreStockForItem(SalesItem item) {
        try {
            Product product = item.getProduct();
            String gtin = product.getGtin();
            Long qty = item.getSalesQuantity() != null ? item.getSalesQuantity().longValue() : 0L;

            if (qty <= 0) {
                log.warn("⚠️ [{}] 환불 수량이 0입니다. 복원 스킵", product.getProductName());
                return;
            }

            // ✅ 유통기한 결정 (기존 LOT 있으면 그대로, 없으면 +6개월)
            LocalDate expDate = (item.getStk() != null && item.getStk().getLot() != null)
                    ? item.getStk().getLot().getExpDate()
                    : LocalDate.now().plusMonths(6);

            // ✅ LOT 조회 or 생성
            Lot lot = lotRepository.findByProduct_GtinAndExpDateAndStatus(gtin, expDate, LotStatus.ACTIVE)
                    .orElseGet(() -> {
                        Lot newLot = Lot.builder()
                                .product(product)
                                .expDate(expDate)
                                .status(LotStatus.ACTIVE)
                                .build();
                        return lotRepository.save(newLot);
                    });

            // ✅ STK 조회 or 생성
            STK stk = stkRepository.findByLot_LotId(lot.getLotId())
                    .orElseGet(() -> {
                        STK newStk = STK.builder()
                                .product(product)
                                .lot(lot)
                                .quantity(0L)
                                .build();
                        return stkRepository.save(newStk);
                    });

            // ✅ 수량 복원
            Long beforeQty = stk.getQuantity() != null ? stk.getQuantity() : 0L;
            stk.setQuantity(beforeQty + qty);
            stk.setLastUpdatedAt(java.time.LocalDateTime.now());
            stkRepository.save(stk);

            // ✅ LOT 활성화 보장
            lot.setStatus(LotStatus.ACTIVE);
            lotRepository.save(lot);

            log.info("✅ [{}] 재고 복원 완료: +{}개 ({} → {}) expDate={}",
                    product.getProductName(), qty, beforeQty, stk.getQuantity(), expDate);

        } catch (Exception e) {
            log.error("❌ [{}] 재고 복원 중 오류: {}", item.getProduct().getProductName(), e.getMessage(), e);
            throw e;
        }
    }
}
