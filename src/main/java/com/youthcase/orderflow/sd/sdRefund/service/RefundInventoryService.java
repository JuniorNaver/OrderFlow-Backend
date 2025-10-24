package com.youthcase.orderflow.sd.sdRefund.service;

import com.youthcase.orderflow.gr.status.LotStatus;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.gr.domain.Lot;
import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import com.youthcase.orderflow.gr.repository.LotRepository;
import com.youthcase.orderflow.stk.repository.STKRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundInventoryService {

    private final STKRepository stkRepository;
    private final LotRepository lotRepository;

    /**
     * ♻️ 환불 시 재고 복원
     * @param item 환불된 판매 항목
     * @param expDate 유통기한 (프론트 또는 기존 LOT 기준)
     */
    public void restoreStock(SalesItem item, LocalDate expDate) {
        Product product = item.getProduct();

        // 1️⃣ 기존 LOT 재고 찾기 (GTIN + EXP_DATE)
        STK stk = stkRepository.findByProduct_GtinAndLot_ExpDate(product.getGtin(), expDate)
                .orElse(null);

        if (stk != null) {
            // 2️⃣ 기존 LOT → 재고 증가
            Long newQty = stk.getQuantity() + item.getSalesQuantity();
            stk.setQuantity(newQty);
            stk.setLastUpdatedAt(java.time.LocalDateTime.now());
            stkRepository.save(stk);

            log.info("♻️ 기존 LOT 재고 복원 완료: {} / LOT={} / +{}",
                    product.getProductName(), expDate, item.getSalesQuantity());
        } else {
            // 3️⃣ 새로운 LOT 생성
            Lot newLot = new Lot();
            newLot.setProduct(product);
            newLot.setExpDate(expDate);
            newLot.setQty((long) item.getSalesQuantity());
            newLot.setStatus(LotStatus.RETURNED);
            newLot.setCreatedAt(OffsetDateTime.now());
            newLot.setUpdatedAt(OffsetDateTime.now());
            lotRepository.save(newLot);

            // 4️⃣ STK 생성 (정적 팩토리 메서드 사용)
            STK newStk = STK.createForRefund(product, newLot, item.getSalesQuantity());
            stkRepository.save(newStk);

            log.info("🆕 새 LOT 생성 + 재고 복원 완료: {} / 유통기한 {} / 수량 {}",
                    product.getProductName(), expDate, item.getSalesQuantity());
        }
    }
}
