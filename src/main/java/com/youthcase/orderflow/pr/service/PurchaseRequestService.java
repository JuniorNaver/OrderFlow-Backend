/**
 * PurchaseRequestService
 * -----------------------
 * 🧠 발주 요청(PR) 비즈니스 로직 서비스
 * - 기존 발주 생성/조회 로직 유지
 * - BI 추천 발주는 보조 기능으로 추가 (기존 흐름 영향 없음)
 */
package com.youthcase.orderflow.pr.service;

import com.youthcase.orderflow.bi.dto.RecommendDTO;
import com.youthcase.orderflow.bi.repository.forecast.BIForecastRepository;
import com.youthcase.orderflow.bi.service.recommend.BIRecommendBatchService;
import com.youthcase.orderflow.bi.service.recommend.BIRecommendService;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.product.service.NotFoundException;
import com.youthcase.orderflow.pr.domain.PurchaseRequest;
import com.youthcase.orderflow.pr.dto.PurchaseRequestCreateDto;
import com.youthcase.orderflow.pr.dto.PurchaseRequestDto;
import com.youthcase.orderflow.pr.mapper.PurchaseRequestMapper;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.pr.repository.PurchaseRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseRequestService {

    private final PurchaseRequestRepository prRepository;
    private final ProductRepository productRepository;

    // 🔒 점장 이상만, 그리고 본인 점포만 발주 가능
    @PreAuthorize("(hasAuthority('PR_ORDER') or hasRole('ADMIN')) and @storeGuard.canAccess(#auth, #storeId)")
    public PurchaseRequestDto placeOrder(String storeId, PurchaseRequestCreateDto dto, Authentication auth) {

        Product p = productRepository.findById(dto.gtin())
                .orElseThrow(() -> new NotFoundException("상품 없음: " + dto.gtin()));

        // 비즈 규칙 예시
        if (dto.qty() <= 0) throw new IllegalArgumentException("발주 수량은 1 이상이어야 합니다.");
        if (Boolean.FALSE.equals(p.getOrderable())) {
            throw new IllegalStateException("해당 상품은 발주 불가 상태입니다.");
        }

        PurchaseRequest pr = PurchaseRequest.create(storeId, dto.gtin(), dto.qty(), dto.expectedDate());
        prRepository.save(pr);
        return PurchaseRequestMapper.toDto(pr);
    }

    // 발주 조회는 읽기 권한
    // 📋 발주 리스트 조회
    @PreAuthorize("hasAuthority('PR_READ')")
    @Transactional(readOnly = true)
    public Page<PurchaseRequestDto> listOrders(String storeId, Pageable pageable) {
        return prRepository.findByStoreId(storeId, pageable).map(PurchaseRequestMapper::toDto);
    }

    // 🔍 발주 단건 조회
    @PreAuthorize("hasAuthority('PR_READ')")
    @Transactional(readOnly = true)
    public PurchaseRequestDto getOrder(Long id) {
        return prRepository.findById(id)
                .map(PurchaseRequestMapper::toDto)
                .orElseThrow(() -> new NotFoundException("발주 없음: " + id));
    }

    // 🔗BI 기반 발주 추천 로직
    // ---------------------------------------------------------------------
    // 🌟 [보조 기능] BI 기반 추천 발주 연동 (기존 로직에는 영향 없음)
    // ---------------------------------------------------------------------

    private final BIRecommendBatchService recommendBatchService;
    private final BIRecommendService recommendService;     // ✅ 결과 조회용
    private final BIForecastRepository forecastRepository; // (참조 가능)

    /**
     * 🔁 BI 기반 추천 발주 자동 갱신 및 조회
     * - PR 진입 시 혹은 수동 호출 시, 최신 예측 기반으로 추천 데이터를 갱신.
     * - 기존 발주 흐름에는 영향을 주지 않음.
     *
     * @param storeId 점포 ID
     * @param from 시작일 (YYYYMMDD)
     * @param to 종료일 (YYYYMMDD)
     * @return 추천 발주 결과 리스트
     */
    public List<RecommendDTO> loadRecommendedOrders(Long storeId, String from, String to) {
        try {
            // ① 재고 정보 (임시 mock — 추후 MM_STK 연동)
            Map<Long, BigDecimal> stockData = Map.of(
                    1001L, BigDecimal.valueOf(5),
                    1002L, BigDecimal.valueOf(10)
            );

            // ② 기준 판매량 (전월 실적 등, 추후 FACT_SALES_DAILY 집계 사용)
            Map<Long, BigDecimal> baselineData = Map.of(
                    1001L, BigDecimal.valueOf(20),
                    1002L, BigDecimal.valueOf(15)
            );

            // ③ 이벤트 영향 계수 (추후 BI_FACTOR_COEF 연동)
            Map<Long, BigDecimal> factorCoefData = Map.of(
                    1001L, BigDecimal.valueOf(1.3), // 행사 상품
                    1002L, BigDecimal.valueOf(1.0)
            );

            // ④ BI 추천 결과 자동 갱신 (v2 로직)
            recommendBatchService.generateRecommendationsV2(
                    storeId,
                    from,
                    to,
                    stockData,
                    baselineData,
                    factorCoefData
            );

            // ⑤ 최신 추천 리스트 조회 및 반환
            return recommendService.getRecommendations(storeId, from, to);

        } catch (Exception e) {
            System.err.println("[BIRecommend] 추천 발주 갱신 중 오류: " + e.getMessage());
            return List.of(); // 오류 시 빈 리스트 반환
        }
    }
}