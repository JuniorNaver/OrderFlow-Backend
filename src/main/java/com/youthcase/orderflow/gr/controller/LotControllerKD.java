//// src/backend/src/main/java/com/orderflow/receipt/controller/LotController.java
//package com.youthcase.orderflow.gr.controller;
//
//import com.orderflow.common.dto.ApiResponse;
//import com.orderflow.receipt.dto.LotDto;
//import com.orderflow.receipt.service.LotService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
///**
// * LOT 관리 컨트롤러
// * MM_GR_004: LOT 관리
// */
//@Tag(name = "Lot", description = "LOT 관리 API")
//@RestController
//@RequestMapping("/lots")
//@RequiredArgsConstructor
//public class LotControllerKD {
//
//    private final LotService lotService;
//
//    @Operation(summary = "LOT 목록 조회", description = "모든 LOT 목록을 페이징하여 조회")
//    @GetMapping
//    public ResponseEntity<ApiResponse<Page<LotDto>>> getLots(
//            @PageableDefault(size = 20) Pageable pageable) {
//        Page<LotDto> lots = lotService.getLots(pageable);
//        return ResponseEntity.ok(ApiResponse.success(lots));
//    }
//
//    @Operation(summary = "LOT 목록 조회 (상품별)", description = "특정 상품의 사용 가능한 LOT 목록 조회")
//    @GetMapping("/product/{productCode}")
//    public ResponseEntity<ApiResponse<List<LotDto>>> getLotsByProduct(@PathVariable String productCode) {
//        List<LotDto> lots = lotService.getLotsByProduct(productCode);
//        return ResponseEntity.ok(ApiResponse.success(lots));
//    }
//
//    @Operation(summary = "유통기한 임박 LOT 조회", description = "유통기한이 임박한 LOT 목록 조회")
//    @GetMapping("/near-expiry")
//    public ResponseEntity<ApiResponse<List<LotDto>>> getNearExpiryLots(
//            @RequestParam(defaultValue = "7") int days) {
//        List<LotDto> lots = lotService.getNearExpiryLots(days);
//        return ResponseEntity.ok(ApiResponse.success(lots));
//    }
//
//    @Operation(summary = "만료된 LOT 조회", description = "유통기한이 지난 LOT 목록 조회")
//    @GetMapping("/expired")
//    public ResponseEntity<ApiResponse<List<LotDto>>> getExpiredLots() {
//        List<LotDto> lots = lotService.getExpiredLots();
//        return ResponseEntity.ok(ApiResponse.success(lots));
//    }
//
//    @Operation(summary = "LOT 상세 조회", description = "LOT ID로 상세 정보 조회")
//    @GetMapping("/{lotId}")
//    public ResponseEntity<ApiResponse<LotDto>> getLot(@PathVariable Long lotId) {
//        LotDto lot = lotService.getLot(lotId);
//        return ResponseEntity.ok(ApiResponse.success(lot));
//    }
//
//    @Operation(summary = "LOT 번호로 조회", description = "LOT 번호로 상세 정보 조회")
//    @GetMapping("/number/{lotNumber}")
//    public ResponseEntity<ApiResponse<LotDto>> getLotByNumber(@PathVariable String lotNumber) {
//        LotDto lot = lotService.getLotByNumber(lotNumber);
//        return ResponseEntity.ok(ApiResponse.success(lot));
//    }
//
//    @Operation(summary = "LOT 상태 업데이트", description = "모든 LOT의 상태를 유통기한 기준으로 일괄 업데이트")
//    @PostMapping("/update-statuses")
//    @PreAuthorize("hasAnyRole('ADMIN')")
//    public ResponseEntity<ApiResponse<Integer>> updateLotStatuses() {
//        int updatedCount = lotService.updateLotStatuses();
//        return ResponseEntity.ok(ApiResponse.success("LOT statuses updated", updatedCount));
//    }
//
//    @Operation(summary = "LOT 폐기", description = "LOT 폐기 처리")
//    @PostMapping("/{lotId}/dispose")
//    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
//    public ResponseEntity<ApiResponse<Void>> disposeLot(@PathVariable Long lotId) {
//        lotService.disposeLot(lotId);
//        return ResponseEntity.ok(ApiResponse.success("LOT disposed successfully", null));
//    }
//}
