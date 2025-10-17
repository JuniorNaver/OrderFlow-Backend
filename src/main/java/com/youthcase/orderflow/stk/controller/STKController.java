package com.youthcase.orderflow.stk.controller;

import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.dto.ProgressStatusDTO;
import com.youthcase.orderflow.stk.dto.StockResponse;
// [TODO] 재고 생성 및 수정 요청을 위한 DTO (StockRequest)가 필요하지만,
// 현재는 STK를 그대로 사용한다고 가정하고 StockResponse만 적용합니다.
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
    // ... (Capacity, Expiry Status API는 ProgressStatusDTO 반환하므로 유지)
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
    // 📦 기존 재고 CRUD API (반환 타입 STK -> StockResponse로 수정)
    // --------------------------------------------------

    // 1. 재고 전체 조회
// ⭐️ 경로를 "/list/all"로 수정하여 프론트엔드 요청에 대응합니다.
    @GetMapping("/list/all")
    public ResponseEntity<List<StockResponse>> getAllStocks() {
        List<StockResponse> stocks = stkService.findAllStocks().stream()
                .map(StockResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(stocks);
    }

// --------------------------------------------------
// 📦 기존 재고 CRUD API (반환 타입 STK -> StockResponse로 수정)
// --------------------------------------------------
// ⭐️ 기존의 @GetMapping("/") 메서드는 제거하거나 단건 검색을 위해 유지합니다.
// --------------------------------------------------

    // 2. 재고 신규 등록 (입력값은 DTO(StockRequest)로 변경하는 것이 좋으나, 현재는 STK 유지)
    @PostMapping
    public ResponseEntity<StockResponse> createStock(@RequestBody STK stock) { // 👈 반환 타입 수정
        STK createdStock = stkService.createStock(stock);
        // 👈 DTO로 변환하여 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(StockResponse.fromEntity(createdStock));
    }

    // 3. 특정 재고 단건 조회
    @GetMapping("/{stkId}")
    public ResponseEntity<StockResponse> getStockById(@PathVariable Long stkId) { // 👈 반환 타입을 StockResponse로 수정
        try {
            STK stock = stkService.findStockById(stkId);
            return ResponseEntity.ok(StockResponse.fromEntity(stock)); // 👈 DTO 변환
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 4. 재고 정보 수정
    @PutMapping("/{stkId}")
    public ResponseEntity<StockResponse> updateStock(@PathVariable Long stkId, @RequestBody STK stockDetails) { // 👈 반환 타입을 StockResponse로 수정
        try {
            // ... (Service 호출 로직)
            STK updatedStock = stkService.updateStock(stkId, stockDetails);
            return ResponseEntity.ok(StockResponse.fromEntity(updatedStock)); // 👈 DTO 변환
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 5. 재고 삭제 (Void 반환이므로 수정 필요 없음)
    @DeleteMapping("/{stkId}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long stkId) {
        try {
            stkService.deleteStock(stkId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 상품 검색 (이미 StockResponse 반환 중이므로 수정 필요 없음)
    @GetMapping("/search")
    public ResponseEntity<List<StockResponse>> searchByProductName(@RequestParam String name) {
        List<StockResponse> results = stkService.searchByProductName(name)
                .stream()
                .map(StockResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/stk/list/relocation : 위치 변경 필요 재고 목록 조회
     */
    @GetMapping("/list/relocation")
    public ResponseEntity<List<StockResponse>> getRelocationList() {
        // ⭐️ STKService에 해당 로직을 처리하는 메서드가 필요합니다. (예: findRelocationRequiredStocks)
        List<StockResponse> relocationStocks = stkService.findRelocationRequiredStocks().stream()
                .map(StockResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(relocationStocks);
    }
}