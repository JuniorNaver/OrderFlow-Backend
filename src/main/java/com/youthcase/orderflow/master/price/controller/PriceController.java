package com.youthcase.orderflow.master.price.controller;

import com.youthcase.orderflow.master.price.dto.PriceRequestDTO;
import com.youthcase.orderflow.master.price.dto.PriceResponseDTO;
import com.youthcase.orderflow.master.price.service.PriceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 🎯 PriceController
 * - 가격 마스터(매입/매출 단가) 관리 및 조회 컨트롤러
 * - GTIN(=ID) 기준 CRUD + 도메인별 조회 API 제공
 */
@RestController
@RequestMapping("/api/master/price")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    // =========================================================
    // 📌 [C] 신규 등록 (관리자 전용)
    // =========================================================
    @PostMapping
    public ResponseEntity<PriceResponseDTO> createPrice(@Valid @RequestBody PriceRequestDTO request) {
        PriceResponseDTO created = priceService.createPrice(request);
        return ResponseEntity.ok(created);
    }

    // =========================================================
    // 📌 [U] 수정 (관리자 전용)
    // =========================================================
    @PutMapping("/{gtin}")
    public ResponseEntity<PriceResponseDTO> updatePrice(
            @PathVariable String gtin,
            @Valid @RequestBody PriceRequestDTO request
    ) {
        // ✅ PathVariable 우선, DTO의 gtin을 일치시켜줌
        request.setGtin(gtin);
        PriceResponseDTO updated = priceService.updatePrice(request);
        return ResponseEntity.ok(updated);
    }

    // =========================================================
    // 📌 [D] 삭제 (관리자 전용)
    // =========================================================
    @DeleteMapping("/{gtin}")
    public ResponseEntity<Void> deletePrice(@PathVariable String gtin) {
        priceService.deletePrice(gtin);
        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // 📌 [R] 단일 조회 (관리자/PR/PO/통합용)
    // =========================================================
    @GetMapping("/{gtin}")
    public ResponseEntity<PriceResponseDTO> getPrice(@PathVariable String gtin) {
        PriceResponseDTO response = priceService.getPrice(gtin);
        return ResponseEntity.ok(response);
    }

    // =========================================================
    // 📌 [R] 매입가 조회 (PR/PO 모듈용)
    // =========================================================
    @GetMapping("/{gtin}/purchase")
    public ResponseEntity<BigDecimal> getPurchasePrice(@PathVariable String gtin) {
        BigDecimal purchasePrice = priceService.getPurchasePrice(gtin);
        return ResponseEntity.ok(purchasePrice);
    }

    // =========================================================
    // 📌 [R] 매출가 조회 (SD 모듈용)
    // =========================================================
    @GetMapping("/{gtin}/sale")
    public ResponseEntity<BigDecimal> getSalePrice(@PathVariable String gtin) {
        BigDecimal salePrice = priceService.getSalePrice(gtin);
        return ResponseEntity.ok(salePrice);
    }

    // =========================================================
    // 📌 [R] 전체 조회 (관리자용 리스트업)
    // =========================================================
    @GetMapping
    public ResponseEntity<List<PriceResponseDTO>> getAllPrices() {
        List<PriceResponseDTO> prices = priceService.getAllPrices();
        return ResponseEntity.ok(prices);
    }
}
