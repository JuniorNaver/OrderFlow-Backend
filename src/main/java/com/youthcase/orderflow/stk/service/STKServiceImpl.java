package com.youthcase.orderflow.stk.service;

import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.dto.ProgressStatusDTO;
import com.youthcase.orderflow.stk.dto.StockDeductionRequestDTO;
import com.youthcase.orderflow.stk.repository.STKRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class STKServiceImpl implements STKService {

    private final STKRepository stkRepository;

    // --------------------------------------------------
    // 1. 기본 재고 CRUD 메서드
    // --------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<STK> findAllStocks() {
        return stkRepository.findAll();
    }

    @Override
    public STK createStock(STK stock) {
        // [TODO] 재고 생성 시 필요한 비즈니스 로직 추가
        return stkRepository.save(stock);
    }

    @Override
    @Transactional(readOnly = true)
    public STK findStockById(Long stkId) {
        return stkRepository.findById(stkId)
                .orElseThrow(() -> new NoSuchElementException("ID: " + stkId + " 재고를 찾을 수 없습니다."));
    }

    @Override
    public STK updateStock(Long stkId, STK stockDetails) {
        STK existingStock = findStockById(stkId);

        // STK 엔티티의 @Setter가 있다면 setQuantity 사용 가능
        // STK 엔티티의 updateQuantity(Integer newQuantity) 메서드를 사용하는 것이 권장됨
        if (stockDetails.getQuantity() != null) {
            // existingStock.setQuantity(stockDetails.getQuantity()); // 👈 STK 엔티티에 setQuantity가 없다면 오류 발생
            // 임시로 STK 엔티티에 updateQuantity가 있다고 가정하고 사용합니다.
            // (STK 엔티티에 @Setter를 추가하거나, updateQuantity 메서드를 사용하세요.)
        }

        // [TODO] 업데이트 시 필요한 비즈니스 로직 및 변경 추적 로직 추가

        return stkRepository.save(existingStock);
    }

    @Override
    public void deleteStock(Long stkId) {
        STK stock = findStockById(stkId);
        // [TODO] 삭제 전 참조 무결성 및 비즈니스 로직 체크
        stkRepository.delete(stock);
    }

    // --------------------------------------------------
    // 2. STKService 인터페이스에 추가된 기존/차감/조회 메서드 구현
    // --------------------------------------------------

    @Override
    @Transactional
    public void deductStockForSalesOrder(StockDeductionRequestDTO request) {
        // [TODO] 주문에 따른 재고 차감 로직 구현
        System.out.println("LOG: 판매 주문에 따른 재고 차감 작업 수행 - 요청: " + request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<STK> getStockByProductGtin(String gtin) {
        // [TODO] 실제 DB 연동 로직 (예: stkRepository.findByProductGtin(gtin))으로 교체 필요
        return Collections.emptyList();
    }

    // --------------------------------------------------
    // 3. 📊 대시보드 현황 조회 메서드 구현
    // --------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ProgressStatusDTO getCapacityStatus() {
        // [TODO] 창고 용량 조회 로직 구현

        // ⭐️ 요구되는 4개의 인자에 맞춰 수정 (제목 추가)
        return new ProgressStatusDTO("창고 적재 용량 현황", 1000L, 780L, "CBM");
    }

    // STKServiceImpl.java (106행 근처)

    @Override
    @Transactional(readOnly = true)
    public ProgressStatusDTO getExpiryStatus(int days) {
        // [TODO] 유통기한 임박 현황 조회 로직 구현

        // ⭐️ 요구되는 4개의 인자에 맞춰 수정 (제목 추가)
        String title = "유통기한 임박 현황 (" + days + "일 이내)";
        return new ProgressStatusDTO(title, 5000L, 1275L, "개");
    }


    // --------------------------------------------------
    // 4. 유통기한 처리 로직 구현 (LocalDate 타입 사용)
    // --------------------------------------------------

    /**
     * 유통기한이 지난 재고를 폐기 처리합니다. (반환 타입 List<STK>에 맞춤)
     * @param targetDate 기준 날짜
     * @return 폐기 처리된 재고 목록
     */
    @Override
    @Transactional
    public List<STK> disposeExpiredStock(LocalDate targetDate) {
        // [TODO] 실제 DB 로직 구현: targetDate 이전에 만료된 재고를 조회 및 상태 업데이트 후 저장
        System.out.println("LOG: 만료 재고 폐기 처리 작업 (기준일: " + targetDate + ")");
        return Collections.emptyList();
    }

    /**
     * 유통기한 임박 재고의 상태를 갱신합니다. (반환 타입 List<STK>에 맞춤)
     * @param targetDate 기준 날짜
     * @return 상태 갱신된 재고 목록
     */
    @Override
    @Transactional
    public List<STK> markNearExpiryStock(LocalDate targetDate) { // ⭐️ List<STK> 반환 타입에 맞게 수정
        // [TODO] 실제 DB 로직 구현: targetDate를 기준으로 임박 재고를 조회 및 상태 업데이트 후 저장
        System.out.println("LOG: 유통기한 임박 재고 상태 업데이트 작업 (기준일: " + targetDate + ")");
        return Collections.emptyList();
    }

    @Override
    public List<STK> searchByProductName(String name) {
        return stkRepository.findByProduct_ProductNameContainingIgnoreCase(name);
    }
}