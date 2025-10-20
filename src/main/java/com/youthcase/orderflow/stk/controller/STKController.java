package com.youthcase.orderflow.stk.controller;

import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.dto.DisposalRequest;
import com.youthcase.orderflow.stk.dto.ProgressStatusDTO;
import com.youthcase.orderflow.stk.dto.StockResponse;
import com.youthcase.orderflow.stk.service.STKService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.youthcase.orderflow.stk.dto.AdjustmentRequest; // ⭐️ 이 임포트가 추가되었는지 확인


import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/stk")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class STKController {

    private final STKService stkService;

    // --------------------------------------------------
    // 📊 대시보드 현황 API
    // --------------------------------------------------

    @GetMapping("/status/capacity")
    public ResponseEntity<ProgressStatusDTO> getCapacityStatus() {
        ProgressStatusDTO status = stkService.getCapacityStatus();
        return ResponseEntity.ok(status);
    }

    @GetMapping("/status/expiry")
    public ResponseEntity<ProgressStatusDTO> getExpiryStatus(@RequestParam(defaultValue = "90") int days) {
        ProgressStatusDTO status = stkService.getExpiryStatus(days);
        return ResponseEntity.ok(status);
    }

    // --------------------------------------------------
    // 📦 재고 목록 및 CRUD API
    // --------------------------------------------------

    // 1. 재고 전체 조회
    @GetMapping("/list/all")
    public ResponseEntity<List<StockResponse>> getAllStocks() {
        List<StockResponse> stocks = stkService.findAllStocks().stream()
                .map(StockResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(stocks);
    }

    // 2. 재고 신규 등록
    @PostMapping
    public ResponseEntity<StockResponse> createStock(@RequestBody STK stock) {
        STK createdStock = stkService.createStock(stock);
        return ResponseEntity.status(HttpStatus.CREATED).body(StockResponse.fromEntity(createdStock));
    }

    // 3. 특정 재고 단건 조회
    @GetMapping("/{stkId}")
    public ResponseEntity<StockResponse> getStockById(@PathVariable Long stkId) {
        try {
            STK stock = stkService.findStockById(stkId);
            return ResponseEntity.ok(StockResponse.fromEntity(stock));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 4. 재고 정보 수정
    @PutMapping("/{stkId}")
    public ResponseEntity<StockResponse> updateStock(@PathVariable Long stkId, @RequestBody STK stockDetails) {
        try {
            STK updatedStock = stkService.updateStock(stkId, stockDetails);
            return ResponseEntity.ok(StockResponse.fromEntity(updatedStock));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 5. 재고 삭제
    @DeleteMapping("/{stkId}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long stkId) {
        try {
            stkService.deleteStock(stkId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 6. 상품명 검색
    @GetMapping("/search")
    public ResponseEntity<List<StockResponse>> searchByProductName(@RequestParam String name) {
        List<StockResponse> results = stkService.searchByProductName(name)
                .stream()
                .map(StockResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(results);
    }

    // 7. 위치 변경 필요 재고 목록 조회
    // 💡 프론트엔드의 fetchRelocationList가 이 경로를 사용한다고 가정하여 수정함
    @GetMapping("/list/relocation-required")
    public ResponseEntity<List<StockResponse>> getRelocationList(@RequestParam(required = false) Long warehouseId) {
        // warehouseId 파라미터는 현재 STKService의 findRelocationRequiredStocks에서 사용되지 않으므로 무시하거나,
        // 필요하다면 서비스 레이어에 로직을 추가해야 합니다. 여기서는 모든 필요한 재고를 반환합니다.
        List<StockResponse> relocationStocks = stkService.findRelocationRequiredStocks().stream()
                .map(StockResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(relocationStocks);
    }

    // --------------------------------------------------
    // 🚨 폐기 및 GTIN 조회 API
    // --------------------------------------------------

    /**
     * GET /api/stk/list/expired : 폐기 예정 재고 목록 조회 (유통기한 만료된 활성 재고)
     * ⭐️ 프론트엔드 stockApi.js의 fetchDisposalList API와 경로 일치
     */
    @GetMapping("/list/expired")
    public ResponseEntity<List<StockResponse>> getExpiredStockList() {
        List<StockResponse> expiredStocks = stkService.findExpiredStocks().stream()
                .map(StockResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(expiredStocks);
    }

    /**
     * GET /api/stk/list/gtin?gtin={gtin} : GTIN으로 해당 제품의 활성 재고 랏(Lot) 목록 조회
     * ⭐️ 프론트엔드 fetchStockByGtin API와 경로 일치 (수정된 경로)
     * @param gtin 스캔된 제품 바코드 (GTIN)
     */
    @GetMapping("/list/gtin") // ⭐️ 404 오류 해결을 위해 경로를 /stock/gtin에서 /list/gtin으로 수정
    public ResponseEntity<List<StockResponse>> getStocksByGtin(@RequestParam String gtin) {
        try {
            List<StockResponse> stocks = stkService.getStockByProductGtin(gtin).stream()
                    .map(StockResponse::fromEntity)
                    .toList();

            if (stocks.isEmpty()) {
                // GTIN에 해당하는 활성 재고가 없으면 404를 반환하여 프론트엔드에서 처리하도록 유도
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            // 서버 내부 오류 발생 시 500 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --------------------------------------------------
    // 🗑️ 폐기 실행 API
    // --------------------------------------------------

    /**
     * POST /api/stk/disposal/execute : 선택된 재고 항목을 폐기 처리합니다.
     */
    @PostMapping("/disposal/execute")
    public ResponseEntity<List<StockResponse>> executeDisposal(@RequestBody DisposalRequest request) {
        try {
            List<STK> updatedStocks = stkService.executeDisposal(request);

            List<StockResponse> response = updatedStocks.stream()
                    .map(StockResponse::fromEntity)
                    .toList();

            return ResponseEntity.ok(response);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 수량 등에 대한 400 Bad Request 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/stk/list/adjustment : 재고 수량 조정이 필요한 목록을 조회합니다.
     */
    @GetMapping("/list/adjustment")
    public ResponseEntity<List<StockResponse>> getAdjustmentRequiredStocks() {
        List<StockResponse> adjustmentStocks = stkService.findStocksRequiringAdjustment().stream()
                .map(StockResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(adjustmentStocks);
    }

    /**
     * POST /api/stk/adjustment/execute : 선택된 재고 항목의 수량을 조정합니다.
     */
    // ⭐️ 재고 조정 실행 API (수정된 랏 ID와 수량만 받아서 처리)
    @PostMapping("/adjustment/execute")
    public ResponseEntity<List<StockResponse>> executeStockAdjustment(@RequestBody AdjustmentRequest request) {
        // [TODO] AdjustmentRequest DTO와 서비스 로직 구현 필요
        // ...
        return ResponseEntity.ok().build();
    }
}