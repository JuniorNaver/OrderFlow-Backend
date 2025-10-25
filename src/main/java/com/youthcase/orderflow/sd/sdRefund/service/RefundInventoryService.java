package com.youthcase.orderflow.sd.sdRefund.service;

import com.youthcase.orderflow.gr.status.LotStatus;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.gr.domain.Lot;
import com.youthcase.orderflow.sd.sdRefund.domain.RefundHeader;
import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import com.youthcase.orderflow.gr.repository.LotRepository;
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
                    .forEach(salesItem -> {
                        String gtin = salesItem.getProduct().getGtin();
                        Product product = salesItem.getProduct();

        if (stk != null) {
            // 2️⃣ 기존 LOT → 재고 증가
            Long newQty = stk.getQuantity() + item.getSalesQuantity();
            stk.setQuantity(newQty);
            stk.setLastUpdatedAt(java.time.LocalDateTime.now());
            stkRepository.save(stk);

                        LocalDate expDate = (salesItem.getStk() != null && salesItem.getStk().getLot() != null)
                                ? salesItem.getStk().getLot().getExpDate()
                                : LocalDate.now().plusMonths(6);

                        // ✅ LOT 조회
                        Lot lot = lotRepository.findByProduct_GtinAndExpDateAndStatus(gtin, expDate, LotStatus.ACTIVE)
                                .orElseGet(() -> {
                                    Lot newLot = Lot.builder()
                                            .product(product)
                                            .expDate(expDate)
                                            .status(LotStatus.ACTIVE)
                                            .build();
                                    return lotRepository.save(newLot);
                                });

                        // ✅ STK 조회
                        STK stk = stkRepository.findByLot_LotId(lot.getLotId())
                                .orElseGet(() -> {
                                    STK newStk = STK.builder()
                                            .product(product)
                                            .lot(lot)
                                            .quantity(0L)
                                            .build();
                                    return stkRepository.save(newStk);
                                });

                        // ✅ null-safe Long 연산
                        Long before = stk.getQuantity() != null ? stk.getQuantity() : 0L;
                        Long addQty = qty != null ? qty : 0L;

                        stk.setQuantity(before + addQty);
                        stkRepository.save(stk);

                        lot.setStatus(LotStatus.ACTIVE);
                        lotRepository.save(lot);

                        log.info("✅ [{}] 재고 복원: +{}개 ({} → {}) expDate={}",
                                product.getProductName(), addQty, before, stk.getQuantity(), expDate);
                    });

            log.info("🧾 [헤더 단위 재고 복원 완료] refundId={}", header.getRefundId());
        } catch (Exception e) {
            log.error("❌ 재고 복원 중 오류: {}", e.getMessage(), e);
            throw e;
        }
    }
}