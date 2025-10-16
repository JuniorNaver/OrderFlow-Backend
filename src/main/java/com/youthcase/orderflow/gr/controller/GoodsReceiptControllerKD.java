//// src/backend/src/main/java/com/orderflow/receipt/controller/GoodsReceiptController.java
//package com.youthcase.orderflow.gr.controller;
//
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//
///**
// * 입고 관리 컨트롤러
// * MM_GR: 입고 관리
// */
//@Tag(name = "Goods Receipt", description = "입고 관리 API")
//@RestController
//@RequestMapping("/goods-receipts")
//@RequiredArgsConstructor
//public class GoodsReceiptControllerKD {
//
//    private final GoodsReceiptService goodsReceiptService;
//
//    @Operation(summary = "입고 목록 조회", description = "모든 입고 목록을 페이징하여 조회")
//    @GetMapping
//    public ResponseEntity<ApiResponse<Page<GrHeaderDto>>> getGoodsReceipts(
//            @PageableDefault(size = 20) Pageable pageable) {
//        Page<GrHeaderDto> receipts = goodsReceiptService.getGoodsReceipts(pageable);
//        return ResponseEntity.ok(ApiResponse.success(receipts));
//    }
//
//    @Operation(summary = "입고 목록 조회 (점포별)", description = "특정 점포의 입고 목록 조회")
//    @GetMapping("/store/{storeId}")
//    public ResponseEntity<ApiResponse<Page<GrHeaderDto>>> getGoodsReceiptsByStore(
//            @PathVariable Long storeId,
//            @PageableDefault(size = 20) Pageable pageable) {
//        Page<GrHeaderDto> receipts = goodsReceiptService.getGoodsReceiptsByStore(storeId, pageable);
//        return ResponseEntity.ok(ApiResponse.success(receipts));
//    }
//
//    @Operation(summary = "입고 목록 조회 (발주별)", description = "특정 발주의 입고 목록 조회")
//    @GetMapping("/purchase-order/{poId}")
//    public ResponseEntity<ApiResponse<Page<GrHeaderDto>>> getGoodsReceiptsByPo(
//            @PathVariable Long poId,
//            @PageableDefault(size = 20) Pageable pageable) {
//        Page<GrHeaderDto> receipts = goodsReceiptService.getGoodsReceiptsByPo(poId, pageable);
//        return ResponseEntity.ok(ApiResponse.success(receipts));
//    }
//
//    @Operation(summary = "입고 목록 조회 (기간별)", description = "특정 기간의 입고 목록 조회")
//    @GetMapping("/date-range")
//    public ResponseEntity<ApiResponse<Page<GrHeaderDto>>> getGoodsReceiptsByDateRange(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
//            @PageableDefault(size = 20) Pageable pageable) {
//        Page<GrHeaderDto> receipts = goodsReceiptService.getGoodsReceiptsByDateRange(startDate, endDate, pageable);
//        return ResponseEntity.ok(ApiResponse.success(receipts));
//    }
//
//    @Operation(summary = "입고 상세 조회", description = "입고 ID로 상세 정보 조회")
//    @GetMapping("/{grId}")
//    public ResponseEntity<ApiResponse<GrHeaderDto>> getGoodsReceipt(@PathVariable Long grId) {
//        GrHeaderDto receipt = goodsReceiptService.getGoodsReceipt(grId);
//        return ResponseEntity.ok(ApiResponse.success(receipt));
//    }
//
//    @Operation(summary = "입고번호로 조회", description = "입고번호로 상세 정보 조회")
//    @GetMapping("/number/{grNumber}")
//    public ResponseEntity<ApiResponse<GrHeaderDto>> getGoodsReceiptByNumber(@PathVariable String grNumber) {
//        GrHeaderDto receipt = goodsReceiptService.getGoodsReceiptByNumber(grNumber);
//        return ResponseEntity.ok(ApiResponse.success(receipt));
//    }
//
//    @Operation(summary = "입고 등록", description = "새로운 입고 등록")
//    @PostMapping
//    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
//    public ResponseEntity<ApiResponse<GrHeaderDto>> createGoodsReceipt(
//            @Valid @RequestBody GrCreateRequest request,
//            Authentication authentication) {
//        Long userId = Long.parseLong(authentication.getName());
//        GrHeaderDto created = goodsReceiptService.createGoodsReceipt(request, userId);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponse.success("Goods receipt created successfully", created));
//    }
//
//    @Operation(summary = "입고 취소", description = "입고 취소 (재고 복원)")
//    @DeleteMapping("/{grId}")
//    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
//    public ResponseEntity<ApiResponse<Void>> cancelGoodsReceipt(
//            @PathVariable Long grId,
//            Authentication authentication) {
//        Long userId = Long.parseLong(authentication.getName());
//        goodsReceiptService.cancelGoodsReceipt(grId, userId);
//        return ResponseEntity.ok(ApiResponse.success("Goods receipt cancelled successfully", null));
//    }
//}
