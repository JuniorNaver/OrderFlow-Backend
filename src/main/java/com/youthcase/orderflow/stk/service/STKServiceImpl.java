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
import java.util.stream.Collectors;

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
        // 일반 findAll() 대신, 상세 정보를 미리 로딩하는 메서드를 사용
        return stkRepository.findAllWithDetails();
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

        // STK 엔티티의 자체 메서드를 사용하여 안전하게 수량 업데이트
        if (stockDetails.getQuantity() != null && !stockDetails.getQuantity().equals(existingStock.getQuantity())) {
            existingStock.updateQuantity(stockDetails.getQuantity());
        }

        // 상태 업데이트
        if (stockDetails.getStatus() != null && !stockDetails.getStatus().equals(existingStock.getStatus())) {
            // STK 엔티티에 updateStatus(String) 메서드가 있다고 가정하고 사용합니다.
            existingStock.updateStatus(stockDetails.getStatus());
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
        // GTIN과 수량 > 0 조건을 만족하는 재고를 유통기한 오름차순으로 조회
        return stkRepository.findByProduct_GtinAndQuantityGreaterThanOrderByLot_ExpDateAsc(gtin, 0);
    }

    // --------------------------------------------------
    // 3. 📊 대시보드 현황 조회 메서드 구현
    // --------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ProgressStatusDTO getCapacityStatus() {
        // [TODO: 실제 구현 시]
        Long totalCapacity = 1000L; // 예시: WarehouseRepository 등에서 조회
        Long usedCapacity = stkRepository.sumActiveQuantity(); // 활성 수량 합계 조회

        return new ProgressStatusDTO("창고 적재 용량 현황", totalCapacity, usedCapacity, "CBM");
    }

    @Override
    @Transactional(readOnly = true)
    public ProgressStatusDTO getExpiryStatus(int days) {
        // [TODO: 실제 구현 시]
        LocalDate expiryLimitDate = LocalDate.now().plusDays(days);
        List<STK> nearExpiryStocks = stkRepository.findNearExpiryActiveStock(expiryLimitDate);

        Long totalActiveStock = stkRepository.sumActiveQuantity();
        Long nearExpiryQuantity = nearExpiryStocks.stream()
                .mapToLong(STK::getQuantity)
                .sum();

        String title = "유통기한 임박 현황 (" + days + "일 이내)";
        return new ProgressStatusDTO(title, totalActiveStock, nearExpiryQuantity, "개");
    }


    // --------------------------------------------------
    // 4. 유통기한 처리 로직 구현 (LocalDate 타입 사용)
    // --------------------------------------------------

    /**
     * 유통기한이 지난 재고를 폐기 처리합니다.
     * @param targetDate 기준 날짜
     * @return 폐기 처리된 재고 목록
     */
    @Override
    @Transactional
    public List<STK> disposeExpiredStock(LocalDate targetDate) {
        // 1. targetDate 이전에 만료된 활성 재고 조회
        List<STK> expiredStocks = stkRepository.findExpiredActiveStockBefore(targetDate);

        // 2. 상태를 'DISPOSED'로 변경하고 수량을 0으로 설정
        expiredStocks.forEach(stock -> {
            stock.updateStatus("DISPOSED");
            stock.setQuantity(0); // 수량 필드에 @Setter가 있다고 가정
        });

        // 3. 변경사항 저장
        return stkRepository.saveAll(expiredStocks);
    }

    /**
     * 유통기한 임박 재고의 상태를 갱신합니다.
     * @param targetDate 임박 기준으로 삼을 날짜 (예: 오늘 + 90일)
     * @return 상태 갱신된 재고 목록
     */
    @Override
    @Transactional
    public List<STK> markNearExpiryStock(LocalDate targetDate) {
        // 1. targetDate까지 임박 재고 조회 (현재 날짜 포함)
        List<STK> nearExpiryStocks = stkRepository.findNearExpiryActiveStock(targetDate);

        // 2. 상태를 'NEAR_EXPIRY'로 변경
        nearExpiryStocks.forEach(stock -> {
            stock.updateStatus("NEAR_EXPIRY");
        });

        // 3. 변경사항 저장
        return stkRepository.saveAll(nearExpiryStocks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<STK> searchByProductName(String name) {
        return stkRepository.findByProduct_ProductNameContainingIgnoreCase(name);
    }

    /**
     * 위치 변경이 필요한 재고 목록을 조회합니다.
     * (예: 보관 조건이 맞지 않거나, 비효율적인 위치에 있는 재고)
     */
    @Override
    @Transactional(readOnly = true)
    public List<STK> findRelocationRequiredStocks() {
        // ⭐️ STKRepository에서 특정 조건에 맞는 재고 목록을 조회하는 메서드를 호출해야 합니다.
        // 예시: isRelocationNeeded 필드가 true인 재고를 찾는다고 가정합니다.
        return stkRepository.findByIsRelocationNeededTrue();

        // 💡 또는, 현재 위치 (location)가 비효율적이라고 판단되는 재고를 조회할 수도 있습니다.
        // return stkRepository.findByLocationNotLike("Optimal%");
    }

    @Override
    public STK findByGtin(String gtin) {
        return stkRepository.findByProduct_Gtin(gtin)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));
    }
}