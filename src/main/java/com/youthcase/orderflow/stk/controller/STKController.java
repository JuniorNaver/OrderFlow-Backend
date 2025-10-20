package com.youthcase.orderflow.stk.controller;

import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.dto.ProgressStatusDTO;
import com.youthcase.orderflow.stk.dto.StockResponse;
import com.youthcase.orderflow.stk.service.STKService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/stk")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class STKController {

    private final STKService stkService;

    // --------------------------------------------------
    // 📊 대시보드 현황 API (수정 필요 없음)
    // --------------------------------------------------
    /**
     * GET /api/stk/status/capacity : 창고 적재 용량 현황 조회
     */
    @GetMapping("/status/capacity")
    public ResponseEntity<ProgressStatusDTO> getCapacityStatus() {
        ProgressStatusDTO status = stkService.getCapacityStatus();
        return ResponseEntity.ok(status);
    }

    /**
     * GET /api/stk/status/expiry?days=90 : 유통기한 임박 현황 조회 (기본 90일)
     */
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

    // 6. 상품 검색
    @GetMapping("/search")
    public ResponseEntity<List<StockResponse>> searchByProductName(@RequestParam String name) {
        List<StockResponse> results = stkService.searchByProductName(name)
                .stream()
                .map(StockResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(results);
    }

    // 7. 위치 변경 필요 재고 목록 조회
    @GetMapping("/list/relocation")
    public ResponseEntity<List<StockResponse>> getRelocationList() {
        List<StockResponse> relocationStocks = stkService.findRelocationRequiredStocks().stream()
                .map(StockResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(relocationStocks);
    }

    // --------------------------------------------------
    // 🚨 STKController.java에 추가된 항목 (404 오류 해결)
    // --------------------------------------------------

    /**
     * GET /api/stk/list/expired : 폐기 예정 재고 목록 조회 (유통기한 만료된 활성 재고)
     * ⭐️ 프론트엔드 DisposalList.jsx의 fetchDisposalList API와 경로를 일치시킵니다.
     */
    @GetMapping("/list/expired") // ⭐️ 404 오류 해결을 위한 엔드포인트 추가
    public ResponseEntity<List<StockResponse>> getExpiredStockList() {
        // ⭐️ STKService에 해당 로직을 처리하는 메서드가 필요합니다. (예: findExpiredStocks)
        List<StockResponse> expiredStocks = stkService.findExpiredStocks().stream()
                .map(StockResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(expiredStocks);
    @GetMapping("/barcode/{gtin}")
    public ResponseEntity<StockResponse> getStockByBarcode(@PathVariable String gtin) {
        try {
            STK stock = stkService.findByGtin(gtin);
            return ResponseEntity.ok(StockResponse.fromEntity(stock));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}