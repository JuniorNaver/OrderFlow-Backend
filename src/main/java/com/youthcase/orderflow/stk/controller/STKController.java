package com.youthcase.orderflow.stk.controller;

import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.dto.DisposalRequest;
import com.youthcase.orderflow.stk.dto.ProgressStatusDTO;
import com.youthcase.orderflow.stk.dto.StockResponse;
import com.youthcase.orderflow.stk.service.STKService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // STK_READ 권한이 필요합니다.
    private final String READ_AUTH = "hasAuthority('STK_READ')";
    // STK_WRITE 권한이 필요합니다. (쓰기, 수정, 삭제, 폐기 실행 포함)
    private final String WRITE_AUTH = "hasAuthority('STK_WRITE')";

    // --------------------------------------------------
    // 📊 대시보드 현황 API (STK_READ)
    // --------------------------------------------------
    @PreAuthorize(READ_AUTH)
    @GetMapping("/status/capacity")
    public ResponseEntity<ProgressStatusDTO> getCapacityStatus() {
        ProgressStatusDTO status = stkService.getCapacityStatus();
        return ResponseEntity.ok(status);
    }

    @PreAuthorize(READ_AUTH)
    @GetMapping("/status/expiry")
    public ResponseEntity<ProgressStatusDTO> getExpiryStatus(@RequestParam(defaultValue = "90") int days) {
        ProgressStatusDTO status = stkService.getExpiryStatus(days);
        return ResponseEntity.ok(status);
    }

    // --------------------------------------------------
    // 📦 재고 목록 및 CRUD API
    // --------------------------------------------------

    // 1. 재고 전체 조회 (STK_READ)
    @PreAuthorize(READ_AUTH)
    @GetMapping("/list/all")
    public ResponseEntity<List<StockResponse>> getAllStocks() {
        List<StockResponse> stocks = stkService.findAllStocks().stream()
                .map(StockResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(stocks);
    }

    // 2. 재고 신규 등록 (STK_WRITE)
    @PreAuthorize(WRITE_AUTH)
    @PostMapping
    public ResponseEntity<StockResponse> createStock(@RequestBody STK stock) {
        STK createdStock = stkService.createStock(stock);
        return ResponseEntity.status(HttpStatus.CREATED).body(StockResponse.fromEntity(createdStock));
    }

    // 3. 특정 재고 단건 조회 (STK_READ)
    @PreAuthorize(READ_AUTH)
    @GetMapping("/{stkId}")
    public ResponseEntity<StockResponse> getStockById(@PathVariable Long stkId) {
        try {
            STK stock = stkService.findStockById(stkId);
            return ResponseEntity.ok(StockResponse.fromEntity(stock));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 4. 재고 정보 수정 (STK_WRITE)
    @PreAuthorize(WRITE_AUTH)
    @PutMapping("/{stkId}")
    public ResponseEntity<StockResponse> updateStock(@PathVariable Long stkId, @RequestBody STK stockDetails) {
        try {
            STK updatedStock = stkService.updateStock(stkId, stockDetails);
            return ResponseEntity.ok(StockResponse.fromEntity(updatedStock));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 5. 재고 삭제 (STK_WRITE)
    @PreAuthorize(WRITE_AUTH)
    @DeleteMapping("/{stkId}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long stkId) {
        try {
            stkService.deleteStock(stkId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 6. 상품명 검색 (STK_READ)
    @PreAuthorize(READ_AUTH)
    @GetMapping("/search")
    public ResponseEntity<List<StockResponse>> searchByProductName(@RequestParam String name) {
        List<StockResponse> results = stkService.searchByProductName(name)
                .stream()
                .map(StockResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(results);
    }

    // 7. 위치 변경 필요 재고 목록 조회 (STK_READ)
    @PreAuthorize(READ_AUTH)
    @GetMapping("/list/relocation-required")
    public ResponseEntity<List<StockResponse>> getRelocationList(@RequestParam(required = false) Long warehouseId) {
        List<StockResponse> relocationStocks = stkService.findRelocationRequiredStocks().stream()
                .map(StockResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(relocationStocks);
    }

    // --------------------------------------------------
    // 🚨 폐기 및 GTIN 조회 API
    // --------------------------------------------------

    // 폐기 예정 재고 목록 조회 (STK_READ)
    @PreAuthorize(READ_AUTH)
    @GetMapping("/list/expired")
    public ResponseEntity<List<StockResponse>> getExpiredStockList() {
        List<StockResponse> expiredStocks = stkService.findExpiredStocks().stream()
                .map(StockResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(expiredStocks);
    }

    // GTIN 으로 활성 재고 랏 목록 조회 (STK_READ)
    @PreAuthorize(READ_AUTH)
    @GetMapping("/list/gtin")
    public ResponseEntity<List<StockResponse>> getStocksByGtin(@RequestParam String gtin) {
        try {
            List<StockResponse> stocks = stkService.getStockByProductGtin(gtin).stream()
                    .map(StockResponse::fromEntity)
                    .toList();

            if (stocks.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --------------------------------------------------
    // 🗑️ 폐기 실행 API (STK_WRITE)
    // --------------------------------------------------

    // 폐기 실행 (STK_WRITE)
    @PreAuthorize(WRITE_AUTH)
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //판매상품 바코드 조회 (STK_READ)
    @PreAuthorize(READ_AUTH)
    @GetMapping("/barcode/{gtin}")
    public ResponseEntity<StockResponse> getStockByBarcode(@PathVariable String gtin) {
        try {
            STK stk = stkService.findFirstAvailableByGtin(gtin);
            return ResponseEntity.ok(StockResponse.fromEntity(stk));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 재고 수량 조정이 필요한 목록 조회 (STK_READ)
    @PreAuthorize(READ_AUTH)
    @GetMapping("/list/adjustment")
    public ResponseEntity<List<StockResponse>> getAdjustmentRequiredStocks() {
        List<StockResponse> adjustmentStocks = stkService.findStocksRequiringAdjustment().stream()
                .map(StockResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(adjustmentStocks);
    }

    // 수량 조정 실행 (STK_WRITE)
    @PreAuthorize(WRITE_AUTH)
    @PostMapping("/adjustment/execute")
    public ResponseEntity<List<StockResponse>> executeStockAdjustment(@RequestBody AdjustmentRequest request) {
        return ResponseEntity.ok().build();
    }
}
