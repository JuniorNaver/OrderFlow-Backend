package com.youthcase.orderflow.stk.controller;

import com.youthcase.orderflow.stk.domain.STK;        // domain 패키지 참조
import com.youthcase.orderflow.stk.service.STKService; // service 패키지 참조
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/stk")
@RequiredArgsConstructor
public class STKController {

    private final STKService stkService;

    // 1. 재고 전체 조회
    @GetMapping
    public ResponseEntity<List<STK>> getAllStocks() {
        List<STK> stocks = stkService.findAllStocks();
        return ResponseEntity.ok(stocks);
    }

    // 2. 재고 신규 등록
    @PostMapping
    public ResponseEntity<STK> createStock(@RequestBody STK stock) {
        STK createdStock = stkService.createStock(stock);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStock);
    }

    // 3. 특정 재고 단건 조회
    @GetMapping("/{stkId}")
    public ResponseEntity<STK> getStockById(@PathVariable Long stkId) {
        try {
            STK stock = stkService.findStockById(stkId);
            return ResponseEntity.ok(stock);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 4. 재고 정보 수정
    @PutMapping("/{stkId}")
    public ResponseEntity<STK> updateStock(@PathVariable Long stkId, @RequestBody STK stockDetails) {
        try {
            STK updatedStock = stkService.updateStock(stkId, stockDetails);
            return ResponseEntity.ok(updatedStock);
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
}