package com.youthcase.orderflow.stk.service;

import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.dto.DisposalRequest;
import com.youthcase.orderflow.stk.dto.ProgressStatusDTO;
import com.youthcase.orderflow.stk.dto.StockDeductionRequestDTO;
import com.youthcase.orderflow.stk.repository.STKRepository;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.pr.repository.LotRepository; // LotRepository import
import com.youthcase.orderflow.master.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class STKServiceImpl implements STKService {

    private final STKRepository stkRepository;
    private final ProductRepository productRepository;
    private final LotRepository lotRepository;
    private final WarehouseRepository warehouseRepository;

    // --------------------------------------------------
    // 📊 대시보드 현황 API 구현
    // --------------------------------------------------

    @Override
    public ProgressStatusDTO getCapacityStatus() {
        return new ProgressStatusDTO("창고 적재 용량 현황", 1000L, 780L, "CBM");
    }

    @Override
    public ProgressStatusDTO getExpiryStatus(int days) {
        LocalDate limitDate = LocalDate.now().plusDays(days);
        List<STK> nearExpiryStocks = stkRepository.findNearExpiryActiveStock(limitDate);

        Long currentQuantity = nearExpiryStocks.stream().mapToLong(STK::getQuantity).sum();
        Long totalQuantity = stkRepository.sumActiveQuantity();

        return new ProgressStatusDTO(
                "유통기한 임박 현황",
                totalQuantity,
                currentQuantity,
                "개"
        );
    }

    // --------------------------------------------------
    // 📦 재고 CRUD 및 기타 API 구현
    // --------------------------------------------------

    @Override
    public List<STK> findAllStocks() {
        return stkRepository.findAll();
    }

    /**
     * 재고 신규 등록 (CREATE)
     */
    @Override
    @Transactional
    public STK createStock(STK stock) {
        // =======================================================
        // 🚨 ORA-02291 방지를 위한 필수 부모 키의 존재 여부 사전 검증
        // =======================================================

        // 1. Product (GTIN) 검증 (타입: String)
        String gtin = stock.getProduct() != null ? stock.getProduct().getGtin() : null;
        if (gtin == null || !productRepository.existsById(gtin)) {
            throw new IllegalArgumentException("참조하려는 제품 (GTIN) 정보가 DB에 존재하지 않거나 필수입니다: " + gtin);
        }

        // 2. Lot ID 검증 (getId()가 아닌 getLotId()로 가정하여 수정. 실제 Lot 엔티티에 따라 수정 필요)
        Long lotId = stock.getLot() != null ? stock.getLot().getLotId() : null; // ⭐️ getLotId()로 수정 (가정)
        if (lotId == null || !lotRepository.existsById(lotId)) {
            throw new IllegalArgumentException("참조하려는 Lot (ID) 정보가 DB에 존재하지 않거나 필수입니다: " + lotId);
        }

        // 3. Warehouse ID 검증 (타입: String으로 수정)
        String warehouseId = stock.getWarehouse() != null ? stock.getWarehouse().getWarehouseId() : null; // ⭐️ String으로 타입 변경
        if (warehouseId == null || !warehouseRepository.existsById(warehouseId)) {
            throw new IllegalArgumentException("참조하려는 창고 (Warehouse ID) 정보가 DB에 존재하지 않거나 필수입니다: " + warehouseId);
        }

        // 모든 검증 통과 후 저장
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

    /**
     * ⭐️ STKService의 findByGtin(String) 메서드 구현 (반환 타입 STK로 가정)
     */
    @Override
    public STK findByGtin(String gtin) { // ⭐️ 반환 타입을 STK로 수정
        return stkRepository.findTopByProduct_Gtin(gtin)
                .orElseThrow(() -> new NoSuchElementException("GTIN에 해당하는 재고를 찾을 수 없습니다: " + gtin));
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

    // --------------------------------------------------
    // 🗑️ 폐기 및 출고 처리 로직 (추상 메서드 구현 누락 해결)
    // --------------------------------------------------

    /**
     * ⭐️ STKService의 추상 메서드 구현: 유통기한 만료 재고 폐기 처리
     */
    @Override
    @Transactional
    public List<STK> disposeExpiredStock(LocalDate targetDate) { // ⭐️ 구현 추가
        List<STK> expiredStocks = stkRepository.findExpiredActiveStockBefore(targetDate);
        for (STK stock : expiredStocks) {
            // [TODO] 폐기 로직: 재고를 0으로 만들고 상태를 'DISPOSED'로 변경
            stock.setQuantity(0);
            stock.updateStatus("DISPOSED");
            stkRepository.save(stock);
        }
        return expiredStocks;
    }

    /**
     * ⭐️ STKService의 추상 메서드 구현: 유통기한 임박 재고 표시
     */
    @Override
    @Transactional
    public List<STK> markNearExpiryStock(LocalDate targetDate) { // ⭐️ 구현 추가
        List<STK> nearExpiryStocks = stkRepository.findNearExpiryActiveStock(targetDate);
        for (STK stock : nearExpiryStocks) {
            // [TODO] 임박 재고 로직: 상태를 'NEAR_EXPIRY'로 변경
            stock.updateStatus("NEAR_EXPIRY");
            stkRepository.save(stock);
        }
        return nearExpiryStocks;
    }

    @Override
    @Transactional
    public void deductStockForSalesOrder(StockDeductionRequestDTO requestDTO) {
        // ... (로직 생략 없이 유지)
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
    @Transactional
    public List<STK> executeDisposal(DisposalRequest request) {
        // 처리된 STK 객체를 담을 리스트
        List<STK> updatedStocks = new ArrayList<>();

        for (DisposalRequest.DisposalItem item : request.getItems()) {
            Long lotId = item.getLotId();
            int requestedQuantity = item.getQuantity();

            // 1. lotId로 해당하는 STK 객체를 찾습니다. (LotId는 STK와 1:1 또는 STK가 Lot을 참조한다고 가정)
            // 여기서는 Lot 엔티티를 통해 STK를 찾는 대신, STK 엔티티에 lotId를 직접 필터링할 수 있다고 가정합니다.
            // 실제 데이터 모델에 맞게 findByLotId로 수정해야 합니다. (예: stkRepository.findByLot_LotId(lotId))
            Optional<STK> stkOptional = stkRepository.findByLot_LotId(lotId);

            if (stkOptional.isEmpty()) {
                // 해당 Lot ID에 대한 활성 재고가 없는 경우
                throw new NoSuchElementException("Lot ID " + lotId + "에 해당하는 활성 재고를 찾을 수 없습니다.");
            }

            STK stock = stkOptional.get();
            int currentQuantity = stock.getQuantity();

            if (requestedQuantity <= 0 || requestedQuantity > currentQuantity) {
                // 요청 수량이 유효하지 않은 경우
                throw new IllegalArgumentException("Lot ID " + lotId + "에 대한 폐기 요청 수량(" + requestedQuantity + ")이 유효하지 않습니다.");
            }

            // 2. 재고 수량 감소
            int newQuantity = currentQuantity - requestedQuantity;
            stock.setQuantity(newQuantity);

            // 3. 폐기 완료 시 상태 변경
            if (newQuantity == 0) {
                stock.updateStatus("DISPOSED"); // 또는 'INACTIVE', 'RETIRED' 등 폐기 상태
            }

            stkRepository.save(stock);
            updatedStocks.add(stock);
        }

        return updatedStocks;
    }
}