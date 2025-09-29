package com.youthcase.orderflow.stk.service;

import com.youthcase.orderflow.stk.domain.STK;        // domain 패키지 참조
import com.youthcase.orderflow.stk.repository.STKRepository; // repository 패키지 참조
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본은 읽기 전용으로 설정
public class STKService {

    private final STKRepository stkRepository;

    /**
     * 전체 재고 목록 조회
     */
    public List<STK> findAllStocks() {
        return stkRepository.findAll();
    }

    /**
     * 재고 등록 (생성)
     */
    @Transactional // 쓰기 작업이 필요한 메서드에 @Transactional 추가
    public STK createStock(STK stock) {
        // 실제 로직: 필수 FK 객체(Warehouse, Product, Lot) 존재 여부 검증 등
        return stkRepository.save(stock);
    }

    /**
     * 단일 재고 조회
     */
    public STK findStockById(Long stkId) {
        return stkRepository.findById(stkId)
                .orElseThrow(() -> new NoSuchElementException("ID: " + stkId + "에 해당하는 재고를 찾을 수 없습니다."));
    }

    /**
     * 재고 수량 및 상태 수정
     */
    @Transactional
    public STK updateStock(Long stkId, STK updatedStock) {
        STK existingStock = findStockById(stkId);

        // 비즈니스 로직에 따라 필요한 필드만 업데이트
        existingStock.setQuantity(updatedStock.getQuantity());
        existingStock.setStatus(updatedStock.getStatus());
        existingStock.setLastUpdatedAt(updatedStock.getLastUpdatedAt());

        return stkRepository.save(existingStock);
    }

    /**
     * 재고 삭제
     */
    @Transactional
    public void deleteStock(Long stkId) {
        stkRepository.deleteById(stkId);
    }
}