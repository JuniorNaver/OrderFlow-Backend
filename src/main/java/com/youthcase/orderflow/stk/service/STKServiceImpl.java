package com.youthcase.orderflow.stk.service;

import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.repository.STKRepository;
import com.youthcase.orderflow.stk.dto.StockDeductionRequestDTO;
import com.youthcase.orderflow.stk.dto.StockDeductionRequestDTO.DeductionItem;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; // ⬅️ LocalDateTime 임포트 추가!
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;

@Service
@lombok.RequiredArgsConstructor
public class STKServiceImpl implements STKService {

    private final STKRepository stkRepository;
    // private final WarehouseRepository warehouseRepository;

    // ⭐️ 유통기한 임박 재고 상태 변경 로직 (변경 없음)
    @Override
    @Transactional
    public List<STK> markNearExpiryStock(Date targetDate) {

        List<STK> nearExpiryStocks = new ArrayList<>();

        // ... (계산 로직 생략, 기존 코드 유지)
        int defaultNearExpiryDays = 30;
        Calendar cal = Calendar.getInstance();
        cal.setTime(targetDate);
        cal.add(Calendar.DAY_OF_MONTH, defaultNearExpiryDays);
        Date limitDate = cal.getTime();

        List<STK> stocksToMark = stkRepository.findNearExpiryActiveStock(limitDate, targetDate);

        if (stocksToMark.isEmpty()) {
            return new ArrayList<>();
        }

        for (STK stk : stocksToMark) {
            stk.updateStatus("NEAR_EXPIRY"); // 상태 변경
            nearExpiryStocks.add(stk);
        }

        return nearExpiryStocks;
    }

    /**
     * 출고 요청에 따라 재고를 차감하고, 수량이 0이 되면 상태를 INACTIVE로 변경합니다.
     */
    @Override
    @Transactional
    public void deductStockForSalesOrder(StockDeductionRequestDTO request) {

        for (DeductionItem item : request.getItems()) {

            STK targetStk = null;

            try {
                targetStk = stkRepository.findById(1L)
                        .orElseThrow(() -> new EntityNotFoundException("재고 차감 대상 STK를 찾을 수 없습니다."));

                targetStk.deductForDisposal(item.getQuantity());

            } catch (EntityNotFoundException e) {
                System.err.println("출고 재고 항목을 찾을 수 없습니다: " + e.getMessage());
                throw new RuntimeException("재고 차감 실패: 대상 재고 없음", e);
            } catch (IllegalArgumentException e) {
                System.err.println("재고 차감 수량 부족: " + e.getMessage());
                throw new RuntimeException("재고 차감 실패: 수량 부족", e);
            }
        }
    }

    // ... (나머지 CRUD 메서드는 변경 없음)

    @Override
    @Transactional(readOnly = true)
    public STK findStockById(Long stkId) {
        return stkRepository.findById(stkId)
                .orElseThrow(() -> new EntityNotFoundException("재고를 찾을 수 없습니다. STK ID: " + stkId));
    }

    @Override
    @Transactional
    public STK createStock(STK stk) {
        return stkRepository.save(stk);
    }

    // ⭐️ updateStock 메서드에서 LocalDateTime.now() 사용을 위해 임포트가 필요합니다.
    @Override
    @Transactional
    public STK updateStock(Long stkId, STK updatedStk) {
        STK existingStk = this.findStockById(stkId);
        existingStk.updateInfo(
                updatedStk.getQuantity(),
                updatedStk.getStatus(),
                LocalDateTime.now() // LocalDateTime 임포트 필요
        );
        return existingStk;
    }

    @Override
    @Transactional
    public void deleteStock(Long stkId) {
        STK existingStk = this.findStockById(stkId);
        stkRepository.delete(existingStk);
    }

    @Override
    public List<STK> getStockByProductGtin(String gtin) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<STK> findAllStocks() {
        return stkRepository.findAll();
    }

    /**
     * 유통기한 만료 재고 폐기 로직
     */
    @Override
    @Transactional
    public List<STK> disposeExpiredStock(Date targetDate) {

        List<STK> expiredStocks = stkRepository.findExpiredActiveStockBefore(targetDate);

        if (expiredStocks.isEmpty()) {
            return new ArrayList<>();
        }

        STK targetStk = expiredStocks.get(0);

        try {
            targetStk.deductForDisposal(1);

            return List.of(targetStk);

        } catch (IllegalArgumentException e) {
            System.err.println("재고 폐기 오류: " + e.getMessage() + " [STK ID: " + targetStk.getStkId() + "]");
            return new ArrayList<>();
        }
    }
}