/**
 * PurchaseRequestController
 * --------------------------
 * 🛒 발주 요청(PR) API 컨트롤러
 * - 발주 생성 (POST /api/pr/stores/{storeId}/orders)
 * - 추천 발주 자동 갱신 (비동기)
 * - 추천 발주 결과 조회 (GET /api/pr/stores/{storeId}/recommend)
 * --------------------------
 * 💡 프론트엔드 연동 예시
 * React에서 추천 발주 섹션 띄우기 👇
 useEffect(() => {
    axios.get(`/api/pr/stores/${storeId}/recommend`)
      .then(res => setRecommendedOrders(res.data))
      .catch(err => console.error(err));
 }, [storeId]);
 */
package com.youthcase.orderflow.pr.controller;

import com.youthcase.orderflow.pr.dto.PurchaseRequestCreateDto;
import com.youthcase.orderflow.pr.dto.PurchaseRequestDto;
import com.youthcase.orderflow.pr.service.PurchaseRequestService;
import com.youthcase.orderflow.bi.dto.RecommendDTO;
import com.youthcase.orderflow.bi.service.recommend.BIRecommendBatchService;
import com.youthcase.orderflow.bi.service.recommend.BIRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/pr")
@RequiredArgsConstructor
public class PurchaseRequestController {

    private final PurchaseRequestService service;
    private final BIRecommendBatchService recommendBatchService; // 🔄 추천 발주 자동 갱신 서비스
    private final BIRecommendService recommendService;           // 📊 추천 발주 결과 조회 서비스

    /**
     * ✅ 발주 요청 생성 API
     * ---------------------
     * - POST /api/pr/stores/{storeId}/orders
     * - 점포별 신규 발주 요청을 생성한다.
     * - 발주 생성 후 BI 추천 발주 결과를 자동으로 비동기 갱신한다.
     */
    @PostMapping("/stores/{storeId}/orders")
    @PreAuthorize("hasAuthority('PR_ORDER') or hasRole('ADMIN')")
    public PurchaseRequestDto placeOrder(
            @PathVariable String storeId,
            @RequestBody PurchaseRequestCreateDto dto,
            Authentication auth
    ) {
        // 1️⃣ 원래의 발주 생성 로직 (핵심 비즈니스 유지)
        PurchaseRequestDto response = service.placeOrder(storeId, dto, auth);

        // 2️⃣ 비동기 후처리: BI 추천 발주 결과 자동 갱신
        triggerRecommendUpdateAsync(Long.parseLong(storeId));

        // 3️⃣ 원래의 응답 그대로 반환
        return response;
    }

    /**
     * 🔁 BI 추천 발주 자동 갱신 (비동기)
     * -----------------------------------
     * - PR 발주 생성 시 자동으로 BI 추천 데이터를 최신화
     * - 실제 PR 비즈니스 흐름에는 영향 없음
     */
    @Async
    protected void triggerRecommendUpdateAsync(Long storeId) {
        try {
            String from = java.time.LocalDate.now().minusDays(7)
                    .format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
            String to = java.time.LocalDate.now()
                    .format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);

            // 예시: 실제 데이터 연동 전에는 빈 Map 사용
            recommendBatchService.generateRecommendationsV2(
                    storeId,
                    from,
                    to,
                    Collections.emptyMap(),
                    Collections.emptyMap(),
                    Collections.emptyMap()
            );

            System.out.println("[BIRecommend] 자동 추천 발주 갱신 완료 (storeId=" + storeId + ")");

        } catch (Exception e) {
            // 예외는 PR 흐름에 영향을 주지 않도록 무시 (로그만 남김)
            System.err.println("[BIRecommend] 추천 발주 갱신 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 📊 추천 발주 결과 조회 API
     * --------------------------
     * - GET /api/pr/stores/{storeId}/recommend
     * - BI_RECOMMEND_RESULT 테이블에서 최신 추천 데이터를 조회
     * - PR 화면의 사이드패널 또는 BI 대시보드에서 사용 가능
     */
    @GetMapping("/stores/{storeId}/recommend")
    @PreAuthorize("hasAuthority('PR_READ') or hasRole('ADMIN')")
    public ResponseEntity<List<RecommendDTO>> getRecommendedOrders(
            @PathVariable Long storeId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        // 기본 조회 기간: 최근 7일
        String fromKey = (from != null) ? from :
                java.time.LocalDate.now().minusDays(7)
                        .format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        String toKey = (to != null) ? to :
                java.time.LocalDate.now()
                        .format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);

        List<RecommendDTO> results = recommendService.getRecommendations(storeId, fromKey, toKey);
        return ResponseEntity.ok(results);
    }
}
