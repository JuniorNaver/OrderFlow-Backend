package com.youthcase.orderflow.stk.service;

import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.repository.STKRepository;
import com.youthcase.orderflow.stk.dto.StockDeductionRequestDTO; // 🚨 필수: 차감 DTO 임포트

import org.springframework.transaction.annotation.Transactional; // 🚨 필수: Spring의 Transactional 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service // 빈 등록
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본은 읽기 전용으로 설정
public class STKServiceImpl implements STKService { // 인터페이스 구현

    private final STKRepository stkRepository;

    /**
     * 전체 재고 목록 조회
     */
    @Override
    public List<STK> findAllStocks() {
        return stkRepository.findAll();
    }

    /**
     * 재고 등록 (생성)
     */
    @Override
    @Transactional // 쓰기 작업
    public STK createStock(STK stock) {
        // 실제 로직: 필수 FK 객체(Warehouse, Product, Lot) 존재 여부 검증 등
        return stkRepository.save(stock);
    }

    /**
     * 단일 재고 조회
     */
    @Override
    public STK findStockById(Long stkId) {
        return stkRepository.findById(stkId)
                .orElseThrow(() -> new NoSuchElementException("ID: " + stkId + "에 해당하는 재고를 찾을 수 없습니다."));
    }

    /**
     * 재고 수량 및 상태 수정
     */
    @Override
    @Transactional // 쓰기 작업
    public STK updateStock(Long stkId, STK updatedStock) {
        STK existingStock = findStockById(stkId);

        // 🚨 수정: Setter 대신 STK 엔티티에 추가된 updateInfo 메서드 호출
        existingStock.updateInfo(
                updatedStock.getQuantity(),
                updatedStock.getStatus(),
                updatedStock.getLastUpdatedAt()
        );

        return stkRepository.save(existingStock);
    }

    /**
     * 재고 삭제
     */
    @Override
    @Transactional // 쓰기 작업
    public void deleteStock(Long stkId) {
        stkRepository.deleteById(stkId);
    }

    /**
     * [출고 로직] 판매 주문에 따라 재고(STK)를 차감합니다. (FIFO 전략 적용)
     */
    @Transactional
    @Override
    public void deductStockForSalesOrder(StockDeductionRequestDTO request) {

        String gtin = request.getGtin();
        int quantityToDeduct = request.getQuantityToDeduct();

        // 1. 차감 가능한 총 재고 수량 확인 (부족하면 즉시 실패)
        Integer totalAvailableStock = stkRepository.findTotalQuantityByGtin(gtin);

        if (totalAvailableStock == null || totalAvailableStock < quantityToDeduct) {
            throw new IllegalArgumentException(
                    "상품 " + gtin + "에 대한 요청 수량(" + quantityToDeduct + ")만큼의 가용 재고가 부족합니다. 현재 재고: " + (totalAvailableStock != null ? totalAvailableStock : 0)
            );
        }

        // 2. FIFO 순서로 재고 레코드 조회
        List<STK> stocksToDeduct = stkRepository.findAvailableStocksByGtinForFIFO(gtin);

        int remainingDeductQuantity = quantityToDeduct;

        // 3. 순차적으로 재고 차감 및 출고 내역 기록
        for (STK stock : stocksToDeduct) {
            if (remainingDeductQuantity <= 0) break; // 차감 완료 시 루프 종료

            int currentStockQuantity = stock.getQuantity();
            int deductedQuantity;

            if (currentStockQuantity >= remainingDeductQuantity) {
                // 현재 LOT 재고로 모두 차감 가능
                deductedQuantity = remainingDeductQuantity;
                remainingDeductQuantity = 0;
            } else {
                // 현재 LOT 재고를 모두 사용해야 함
                deductedQuantity = currentStockQuantity;
                remainingDeductQuantity -= currentStockQuantity;
            }

            // 4. 재고 엔티티 업데이트 (STK 엔티티의 updateQuantity 메서드 사용)
            stock.updateQuantity(currentStockQuantity - deductedQuantity);
            stkRepository.save(stock);

            // 5. [중요] 출고 내역 (GoodsIssue) 기록
            /* // GoodsIssueService가 있다면 아래와 같이 호출
            goodsIssueService.recordDeduction(
                request.getOrderId(), // 주문 정보
                stock,                // 차감된 STK 레코드
                deductedQuantity      // 차감 수량
            );
            */

            // 재고 수량이 0이 되면 상태를 INACTIVE로 변경
            if (stock.getQuantity() == 0) {
                // 🚨 수정: 엔티티의 markAsInactive() 메서드 호출
                stock.markAsInactive();
                stkRepository.save(stock);
            }
        }
    }
}