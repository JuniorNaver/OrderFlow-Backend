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
import com.youthcase.orderflow.master.product.service.NotFoundException;
import com.youthcase.orderflow.pr.service.PurchaseRequestService;
import com.youthcase.orderflow.bi.dto.RecommendDTO;
import com.youthcase.orderflow.bi.service.recommend.BIRecommendService;
import com.youthcase.orderflow.pr.task.RecommendUpdateJob;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/pr")
@RequiredArgsConstructor
public class PurchaseRequestController {

    private final PurchaseRequestService service;
    private final BIRecommendService recommendService;           // 📊 추천 발주 결과 조회 서비스
    private final RecommendUpdateJob recommendUpdateJob;


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
        var response = service.placeOrder(storeId, dto, auth);
        Long internalId = toLongOrThrow(storeId);
        recommendUpdateJob.trigger(internalId); // ✅ 이거 하나로 끝
        return response;
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
            @PathVariable String storeId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        // 기본 조회 기간: 최근 7일
        String fromKey = (from != null) ? from :
                java.time.LocalDate.now().minusDays(7)
                        .format(DateTimeFormatter.BASIC_ISO_DATE);
        String toKey = (to != null) ? to :
                java.time.LocalDate.now()
                        .format(DateTimeFormatter.BASIC_ISO_DATE);

        // 🔁 BI 쪽이 Long을 요구하므로 숫자 문자열일 경우에만 파싱
        Long internalId = toLongOrThrow(storeId);
        List<RecommendDTO> results = recommendService.getRecommendations(internalId, fromKey, toKey);
        return ResponseEntity.ok(results);

            }
    // ---- helper ----
    private Long toLongOrThrow (String raw){
        String s = (raw == null) ? "" : raw.trim();
        if (!s.matches("^[0-9]+$")) {
            throw new NotFoundException("점포 ID 형식 오류(숫자만 허용): " + raw);
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new NotFoundException("점포 ID 변환 실패: " + raw);
        }
    }
}