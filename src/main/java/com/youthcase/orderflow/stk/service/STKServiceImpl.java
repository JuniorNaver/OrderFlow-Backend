package com.youthcase.orderflow.stk.service;

import com.youthcase.orderflow.gr.domain.GoodsReceiptHeader;
import com.youthcase.orderflow.gr.domain.Lot;
import com.youthcase.orderflow.gr.repository.GoodsReceiptHeaderRepository;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.dto.*;
import com.youthcase.orderflow.stk.repository.STKRepository;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.gr.repository.LotRepository;
import com.youthcase.orderflow.master.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final WarehouseRepository warehouseRepository;
    private final LotRepository lotRepository;
    private final GoodsReceiptHeaderRepository grHeaderRepository;

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
        // ... (생략: 부모 키 존재 여부 검증 로직)

        // 1. Product (GTIN) 검증
        String gtin = stock.getProduct() != null ? stock.getProduct().getGtin() : null;
        if (gtin == null || !productRepository.existsById(gtin)) {
            throw new IllegalArgumentException("참조하려는 제품 (GTIN) 정보가 DB에 존재하지 않거나 필수입니다: " + gtin);
        }

        // 2. Lot ID 검증
        Long lotId = stock.getLot() != null ? stock.getLot().getLotId() : null;
        if (lotId == null || !lotRepository.existsById(lotId)) {
            throw new IllegalArgumentException("참조하려는 Lot (ID) 정보가 DB에 존재하지 않거나 필수입니다: " + lotId);
        }

        // 3. Warehouse ID 검증
        String warehouseId = stock.getWarehouse() != null ? stock.getWarehouse().getWarehouseId() : null;
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
        // 재고가 0보다 큰 활성 재고 랏 목록을 유통기한 순으로 조회
        return stkRepository.findByProduct_GtinAndQuantityGreaterThanOrderByLot_ExpDateAsc(gtin, 0L);
    }

    @Override
    public STK findByGtin(String gtin) {
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
        // 유통기한 만료된 활성 재고 목록을 조회하여 폐기 예정 목록으로 반환
        return stkRepository.findExpiredActiveStockBefore(today);
    }




    // --------------------------------------------------
    // 🗑️ 폐기 및 출고 처리 로직
    // --------------------------------------------------

    // ⭐️ markExpiredStock() 메서드 (STKService에 정의된 것으로 가정하고 @Override 유지)
    @Override
    @Transactional
    public List<STK> markExpiredStock() {
        LocalDate today = LocalDate.now();
        List<STK> expiredStocks = stkRepository.findExpiredActiveStockBefore(today);
        for (STK stock : expiredStocks) {
            stock.updateStatus("EXPIRED");
            stkRepository.save(stock);
        }
        return expiredStocks;
    }

    @Override
    @Transactional
    public List<STK> disposeExpiredStock(LocalDate targetDate) {
        List<STK> expiredStocks = stkRepository.findExpiredActiveStockBefore(targetDate);
        for (STK stock : expiredStocks) {
            stock.setQuantity(0L);
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
        // ... (출고 차감 로직 생략 없이 유지)
        for (StockDeductionRequestDTO.DeductionItem item : requestDTO.getItems()) {
            String gtin = item.getGtin();
            Long requiredQuantity = item.getQuantity();

            List<STK> fifoStocks = stkRepository.findByProduct_GtinAndQuantityGreaterThanOrderByLot_ExpDateAsc(gtin, 0L);

            Long remainingToDeduct = requiredQuantity;

            for (STK stock : fifoStocks) {
                if (remainingToDeduct <= 0) break;

                Long stockQuantity = stock.getQuantity();

                if (stockQuantity >= remainingToDeduct) {
                    stock.setQuantity(stockQuantity - remainingToDeduct);
                    remainingToDeduct = 0L;
                } else {
                    remainingToDeduct -= stockQuantity;
                    stock.setQuantity(0L);
                    stock.updateStatus("INACTIVE");
                }

                stkRepository.save(stock);
            }

            if (remainingToDeduct > 0) {
                throw new RuntimeException("재고 부족: GTIN " + gtin + "에 대해 " + remainingToDeduct + "개가 부족합니다.");
            }
        }
    }

    // --------------------------------------------------
    // 🗑️ 개별 폐기 실행 로직
    // --------------------------------------------------

    @Override
    public STK findFirstAvailableByGtin(String gtin) {
        return stkRepository
                .findByProduct_GtinAndQuantityGreaterThanOrderByLot_ExpDateAsc(gtin, 0L)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 상품의 재고가 없습니다."));
    }

    @Override
    @Transactional
    public List<STK> executeDisposal(DisposalRequest request) {
        List<STK> updatedStocks = new ArrayList<>();

        for (DisposalRequest.DisposalItem item : request.getItems()) {
            Long lotId = item.getLotId();
            Long requestedQuantity = item.getQuantity();

            // Lot ID로 활성 재고를 찾습니다.
            Optional<STK> stkOptional = stkRepository.findByLot_LotIdAndQuantityGreaterThan(lotId, 0L);

            if (stkOptional.isEmpty()) {
                throw new NoSuchElementException("Lot ID " + lotId + "에 해당하는 활성 재고를 찾을 수 없습니다.");
            }

            STK stock = stkOptional.get();
            Long currentQuantity = stock.getQuantity();

            if (requestedQuantity <= 0 || requestedQuantity > currentQuantity) {
                throw new IllegalArgumentException("Lot ID " + lotId + "에 대한 폐기 요청 수량(" + requestedQuantity + ")이 유효하지 않습니다.");
            }

            // 재고 수량 감소 및 상태 변경
            Long newQuantity = currentQuantity - requestedQuantity;
            stock.setQuantity(newQuantity);

            if (newQuantity == 0) {
                stock.updateStatus("DISPOSED");
            }

            stkRepository.save(stock);
            updatedStocks.add(stock);
        }

        return updatedStocks;
    }

    // --------------------------------------------------
    // ⚙️ 재고 조정 실행 로직 (AdjustmentRequest 구현)
    // --------------------------------------------------

    /**
     * ⭐️ 재고 조정 요청을 받아 최종 수량으로 업데이트합니다.
     */
    @Override
    @Transactional
    public List<STK> executeStockAdjustment(AdjustmentRequest request) {
        List<STK> updatedStocks = new ArrayList<>();

        for (AdjustmentRequest.AdjustmentItem item : request.getItems()) {

            Long lotId = item.getLotId();
            Long targetQuantity = item.getTargetQuantity();

            // Lot ID로 조정할 STK 재고 항목을 조회합니다. (재고가 0 이하라도 조회되어야 하므로 findByLot_LotId 사용)
            Optional<STK> stkOptional = stkRepository.findByLot_LotId(lotId);

            STK stock = stkOptional
                    .orElseThrow(() -> new NoSuchElementException("Lot ID " + lotId + "에 해당하는 재고를 찾을 수 없습니다."));

            // 현재 수량과 목표 수량이 다를 때만 처리합니다.
            if (stock.getQuantity() != targetQuantity) {

                // STK 엔티티의 updateQuantity를 사용하여 수량을 업데이트하고 최종 업데이트 시간을 기록합니다.
                stock.updateQuantity(targetQuantity);

                // 조정 후 상태 로직 (비즈니스 요구사항에 맞게 상태를 업데이트)
                if (stock.getQuantity() <= 0) {
                    // 조정 후에도 수량이 0 이하인 경우 (예: 손실 확정)
                    stock.updateStatus("ADJUSTED_TO_INACTIVE");
                } else {
                    // 조정 후 수량이 양수이면 ACTIVE 상태로 복귀
                    stock.updateStatus("ACTIVE");
                }

                stkRepository.save(stock);
                updatedStocks.add(stock);
            }
        }

        return updatedStocks;
    }

    // ⭐️ 재고 조정이 필요한 (수량이 0 이하인) 재고 목록 조회 메서드 구현
    @Override
    public List<STK> findStocksRequiringAdjustment() {
        // 0 이하의 수량을 가진 재고를 조회합니다.
        return stkRepository.findByQuantityLessThanEqual(0L);
    }

    //GR
    @Override
    @Transactional
    public void increaseStock(String warehouseId, String gtin, Long qty, Long lotNo, LocalDate expDate) {
        // ✅ 입고 처리: 재고 증가
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("창고 없음"));
        Product product = productRepository.findByGtin(gtin)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음"));
        Lot lot = lotRepository.findById(lotNo)
                .orElseThrow(() -> new IllegalArgumentException("LOT 없음"));

        // 기존 재고 존재 여부 확인
        Optional<STK> existingOpt = stkRepository.findByWarehouseAndProductAndLot(warehouseId, gtin, lotNo);

        STK stk;
        if (existingOpt.isPresent()) {
            // ✅ 기존 재고가 있으면 수량만 증가
            stk = existingOpt.get();
            Long newQty = stk.getQuantity() + qty;
            stk.setQuantity(newQty);
            stk.setLastUpdatedAt(LocalDateTime.now());
        } else {
            // ✅ 신규 재고 생성
            stk = STK.builder()
                    .warehouse(warehouse)
                    .product(product)
                    .lot(lot)
                    .goodsReceipt(null)
                    .quantity(qty)
                    .hasExpirationDate(expDate != null)
                    .status("ACTIVE")
                    .lastUpdatedAt(LocalDateTime.now())
                    .isRelocationNeeded(false)
                    .location(null)
                    .build();
        }

        stkRepository.save(stk);
    }

    @Override
    @Transactional
    public void decreaseStock(String warehouseId, String gtin, Long qty, Long lotNo, LocalDate expDate) {
        // ✅ 출고 처리: 재고 차감
        STK stk = stkRepository.findByWarehouseAndProductAndLot(warehouseId, gtin, lotNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 재고 없음"));

        Long remain = stk.getQuantity() - qty;
        if (remain < 0) {
            throw new IllegalStateException("재고 수량 부족: " + stk.getProductName());
        }

        stk.setQuantity(remain);
        stk.setLastUpdatedAt(LocalDateTime.now());

        if (remain == 0) stk.setStatus("EMPTY");

        stkRepository.save(stk);
    }

    // ⭐️ STKRequest DTO를 받아 STK 엔티티를 생성하고 저장하는 메서드 구현
    @Override
    public STK createStockFromRequest(STKRequestDTO request) {
        // 1. DTO의 ID를 사용하여 필수 엔티티 조회 (FK 바인딩)
        Product product = productRepository.findById(request.getProductGtin())
                .orElseThrow(() -> new NoSuchElementException("상품(GTIN)을 찾을 수 없습니다: " + request.getProductGtin()));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new NoSuchElementException("창고를 찾을 수 없습니다: " + request.getWarehouseId()));

        Lot lot = lotRepository.findById(request.getLotId())
                .orElseThrow(() -> new NoSuchElementException("랏을 찾을 수 없습니다: " + request.getLotId()));

        GoodsReceiptHeader grHeader = grHeaderRepository.findById(request.getGrHeaderId())
                .orElseThrow(() -> new NoSuchElementException("입고 헤더를 찾을 수 없습니다: " + request.getGrHeaderId()));


        // 2. STK.builder()를 사용하여 엔티티 생성
        STK newStock = STK.builder()
                .product(product)
                .warehouse(warehouse)
                .lot(lot)
                .goodsReceipt(grHeader)

                .quantity(request.getQuantity())
                .status(request.getStatus())
                .location(request.getLocation())
                .hasExpirationDate(request.getHasExpirationDate())
                .lastUpdatedAt(LocalDateTime.now())
                .build();

        // 3. 저장 및 반환
        return stkRepository.save(newStock);
    }
}