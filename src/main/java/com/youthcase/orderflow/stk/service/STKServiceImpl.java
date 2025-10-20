package com.youthcase.orderflow.stk.service;

import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.dto.ProgressStatusDTO;
import com.youthcase.orderflow.stk.dto.StockDeductionRequestDTO;
import com.youthcase.orderflow.stk.repository.STKRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class STKServiceImpl implements STKService {

    private final STKRepository stkRepository;

    // --------------------------------------------------
    // 📊 대시보드 현황 API 구현 (ProgressStatusDTO 반영 수정)
    // --------------------------------------------------

    /**
     * 창고 적재 용량 현황 조회 (title 추가)
     */
    @Override
    public ProgressStatusDTO getCapacityStatus() {
        // [TODO] 실제 DB에서 총 용량과 사용 용량을 조회해야 함

        // ⭐️ ProgressStatusDTO 생성자 수정: title, total, current, unit
        return new ProgressStatusDTO("창고 적재 용량 현황", 1000L, 780L, "CBM");
    }

    /**
     * 유통기한 임박 현황 조회 (title 추가 및 Long 타입 명시)
     * @param days 임박 기준으로 삼을 일 수
     */
    @Override
    public ProgressStatusDTO getExpiryStatus(int days) {
        LocalDate limitDate = LocalDate.now().plusDays(days);
        List<STK> nearExpiryStocks = stkRepository.findNearExpiryActiveStock(limitDate);

        // sum() 결과는 long이므로 Long으로 변환
        Long currentQuantity = nearExpiryStocks.stream().mapToLong(STK::getQuantity).sum();
        Long totalQuantity = stkRepository.sumActiveQuantity();

        // ⭐️ ProgressStatusDTO 생성자 수정: title, total, current, unit
        return new ProgressStatusDTO(
                "유통기한 임박 현황",
                totalQuantity, // Long
                currentQuantity, // Long
                "개"
        );
    }

    // --------------------------------------------------
    // 📦 재고 CRUD 및 기타 API 구현 (생략 - 이전과 동일)
    // --------------------------------------------------

    @Override
    public List<STK> findAllStocks() {
        return stkRepository.findAll();
    }

    @Override
    @Transactional
    public STK createStock(STK stock) {
        return stkRepository.save(stock);
    }

    @Override
    public STK findStockById(Long stkId) {
        return stkRepository.findById(stkId)
                .orElseThrow(() -> new NoSuchElementException("재고 ID를 찾을 수 없습니다: " + stkId));
    }

    @Override
    @Transactional
    public STK updateStock(Long stkId, STK stockDetails) {
        STK existingStock = findStockById(stkId);
        existingStock.setQuantity(stockDetails.getQuantity());
        existingStock.setLocation(stockDetails.getLocation());
        return stkRepository.save(existingStock);
    }

    @Override
    @Transactional
    public void deleteStock(Long stkId) {
        stkRepository.deleteById(stkId);
    }

    @Override
    public List<STK> searchByProductName(String name) {
        return stkRepository.findByProduct_ProductNameContainingIgnoreCase(name);
    }

    @Override
    public List<STK> getStockByProductGtin(String gtin) {
        return stkRepository.findByProduct_GtinAndQuantityGreaterThanOrderByLot_ExpDateAsc(gtin, 0);
    }

    @Override
    public List<STK> findRelocationRequiredStocks() {
        return stkRepository.findByIsRelocationNeededTrue();
    }

    @Override
    public List<STK> findExpiredStocks() {
        LocalDate today = LocalDate.now();
        return stkRepository.findExpiredActiveStockBefore(today);
    }

    @Override
    @Transactional
    public List<STK> disposeExpiredStock(LocalDate targetDate) {
        List<STK> expiredStocks = stkRepository.findExpiredActiveStockBefore(targetDate);
        for (STK stock : expiredStocks) {
            stock.setQuantity(0);
            stock.updateStatus("DISPOSED");
            stkRepository.save(stock);
        }
        return expiredStocks;
    }

    @Override
    @Transactional
    public List<STK> markNearExpiryStock(LocalDate targetDate) {
        List<STK> nearExpiryStocks = stkRepository.findNearExpiryActiveStock(targetDate);
        for (STK stock : nearExpiryStocks) {
            stock.updateStatus("NEAR_EXPIRY");
            stkRepository.save(stock);
        }
        return nearExpiryStocks;
    }

    @Override
    @Transactional
    public void deductStockForSalesOrder(StockDeductionRequestDTO requestDTO) {
        for (StockDeductionRequestDTO.DeductionItem item : requestDTO.getItems()) {
            String gtin = item.getGtin();
            Integer requiredQuantity = item.getQuantity();

            List<STK> fifoStocks = stkRepository.findByProduct_GtinAndQuantityGreaterThanOrderByLot_ExpDateAsc(gtin, 0);

            int remainingToDeduct = requiredQuantity;

            for (STK stock : fifoStocks) {
                if (remainingToDeduct <= 0) break;

                int stockQuantity = stock.getQuantity();

                if (stockQuantity >= remainingToDeduct) {
                    stock.setQuantity(stockQuantity - remainingToDeduct);
                    remainingToDeduct = 0;
                } else {
                    remainingToDeduct -= stockQuantity;
                    stock.setQuantity(0);
                    stock.updateStatus("INACTIVE");
                }

                stkRepository.save(stock);
            }

            if (remainingToDeduct > 0) {
                throw new RuntimeException("재고 부족: GTIN " + gtin + "에 대해 " + remainingToDeduct + "개가 부족합니다.");
            }
        }
    }

    @Override
    public STK findByGtin(String gtin) {
        return stkRepository.findByProduct_Gtin(gtin)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));
    }
}