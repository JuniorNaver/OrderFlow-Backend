/**
 * BIRecommendController
 * ----------------------
 * 🛍️ 추천 발주 결과 REST API 컨트롤러
 * - 역할: 추천 발주 결과 조회/갱신 엔드포인트 제공
 * - URL:
 *   - GET  /bi/recommend               : 기간 내 추천 리스트 조회
 *   - POST /bi/recommend/refresh       : v1 로직으로 갱신 (예측-재고)
 *   - POST /bi/recommend/refresh/v2    : v2 로직으로 갱신 (상승률/영향계수 반영)
 * - Params:
 *   - storeId(Long), from(String: YYYYMMDD), to(String: YYYYMMDD)
 */
package com.youthcase.orderflow.bi.controller;

import com.youthcase.orderflow.bi.dto.RecommendDTO;
import com.youthcase.orderflow.bi.dto.RecommendRefreshRequest;
import com.youthcase.orderflow.bi.service.recommend.BIRecommendBatchService;
import com.youthcase.orderflow.bi.service.recommend.BIRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bi/recommend")
@RequiredArgsConstructor
public class BIRecommendController {

    private final BIRecommendService recommendService;           // 조회 담당 서비스
    private final BIRecommendBatchService recommendBatchService; // 갱신/계산 담당 서비스

    /**
     * 특정 기간의 추천 발주 데이터 조회
     * - 현재 저장된 추천 결과를 반환 (정렬/페이징은 프론트 요구에 따라 확장)
     */
    @GetMapping
    public ResponseEntity<List<RecommendDTO>> getRecommendations(
            @RequestParam Long storeId,
            @RequestParam String from,
            @RequestParam String to
    ) {
        return ResponseEntity.ok(recommendService.getRecommendations(storeId, from, to));
    }

    /**
     * 추천 발주 데이터 갱신 API (v1)
     * ------------------------------
     * 🔁 예측 데이터와 재고 데이터를 기반으로 추천 발주 결과를 새로 생성.
     * - URL: POST /bi/recommend/refresh
     * - Body: {
     *     "storeId": 1,
     *     "from": "20251001",
     *     "to": "20251007",
     *     "stockData": { "1001": 5, "1002": 10 }
     *   }
     */
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshRecommendations(@RequestBody RecommendRefreshRequest request) {
        recommendBatchService.generateRecommendations(
                request.getStoreId(),
                request.getFrom(),
                request.getTo(),
                request.getStockData()
        );
        return ResponseEntity.ok("추천 발주 결과가 갱신되었습니다.");
    }

    /**
     * 추천 발주 데이터 갱신 API (v2)
     * ------------------------------
     * 🔁 예측 상승률/이벤트 영향 계수까지 고려한 고도화 로직으로 추천 생성
     * - Body에 baselineData(기준 판매량), factorCoefData(영향계수) 포함
     */
    @PostMapping("/refresh/v2")
    public ResponseEntity<String> refreshRecommendationsV2(@RequestBody RecommendRefreshRequest request) {
        recommendBatchService.generateRecommendationsV2(
                request.getStoreId(),
                request.getFrom(),
                request.getTo(),
                request.getStockData(),
                request.getBaselineData(),
                request.getFactorCoefData()
        );
        return ResponseEntity.ok("추천 발주 결과(v2)가 갱신되었습니다.");
    }
}
