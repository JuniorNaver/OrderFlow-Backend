/**
 * BIRecommendBatchService
 * ------------------------
 * 🔄 예측 판매량과 재고/기준/영향계수를 기반으로 추천 발주 결과를 자동 계산하는 서비스.
 * - v1: BI_FORECAST_RESULT(예측) - 재고(MM_STK) > 0 인 경우만 추천
 * - v2: (예측 상승률 >= 임계치 OR 이벤트 영향계수 > 1.0) && 재고부족 인 경우 추천
 */
package com.youthcase.orderflow.bi.service.recommend;

import com.youthcase.orderflow.bi.domain.forecast.BIForecastResult;
import com.youthcase.orderflow.bi.domain.recommend.BIRecommendResult;
import com.youthcase.orderflow.bi.repository.forecast.BIForecastRepository;
import com.youthcase.orderflow.bi.repository.recommend.BIRecommendRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BIRecommendBatchService {

    private final BIForecastRepository forecastRepository;
    private final BIRecommendRepository recommendRepository;

    /**
     * v1: 추천 발주량 생성 및 저장
     * @param storeId 점포 ID
     * @param from 시작일 (YYYYMMDD)
     * @param to 종료일 (YYYYMMDD)
     * @param currentStockData Map<ProductId, 재고수량>
     */
    @Transactional
    public void generateRecommendations(Long storeId, String from, String to, Map<Long, BigDecimal> currentStockData) {

        // 1️⃣ 지정된 기간의 예측 판매량 조회
        List<BIForecastResult> forecasts =
                forecastRepository.findByStoreIdAndPeriodStartKeyGreaterThanEqualAndPeriodEndKeyLessThanEqual(
                        storeId, from, to
                );

        // 2️⃣ 각 상품별 추천 발주량 계산
        for (BIForecastResult forecast : forecasts) {
            BigDecimal forecastQty = forecast.getForecastQty();
            BigDecimal stockQty = currentStockData.getOrDefault(forecast.getProductId(), BigDecimal.ZERO);

            BigDecimal recommended = forecastQty.subtract(stockQty);
            if (recommended.compareTo(BigDecimal.ZERO) > 0) { // 예측 - 재고 > 0인 경우만 추천
                BIRecommendResult result = BIRecommendResult.builder()
                        .storeId(storeId)
                        .productId(forecast.getProductId())
                        .periodStartKey(forecast.getPeriodStartKey())
                        .periodEndKey(forecast.getPeriodEndKey())
                        .forecastQty(forecastQty)
                        .currentStockQty(stockQty)
                        .recommendedOrderQty(recommended)
                        .calculatedAt(LocalDateTime.now())
                        .build();
                recommendRepository.save(result);
            }
        }
    }

    /**
     * v2: 개선된 추천 발주 계산 로직
     * --------------------------------
     * - 예측 판매량이 과거 평균 대비 상승한 상품(기본 10%↑)
     * - 또는 이벤트 영향 계수가 상향(>1.0)된 상품
     * - 재고 부족(예측 > 재고) 조건 동시 만족 시 추천
     */
    @Transactional
    public void generateRecommendationsV2(Long storeId, String from, String to,
                                          Map<Long, BigDecimal> currentStockData,
                                          Map<Long, BigDecimal> baselineData,
                                          Map<Long, BigDecimal> factorCoefData) {

        List<BIForecastResult> forecasts =
                forecastRepository.findByStoreIdAndPeriodStartKeyGreaterThanEqualAndPeriodEndKeyLessThanEqual(
                        storeId, from, to
                );

        for (BIForecastResult forecast : forecasts) {
            BigDecimal forecastQty = forecast.getForecastQty();
            BigDecimal baseline = baselineData.getOrDefault(forecast.getProductId(), BigDecimal.ZERO);
            BigDecimal coef = factorCoefData.getOrDefault(forecast.getProductId(), BigDecimal.ONE);
            BigDecimal stock = currentStockData.getOrDefault(forecast.getProductId(), BigDecimal.ZERO);

            // 상승률 계산 (baseline=0 방지)
            BigDecimal growthRate = BigDecimal.ZERO;
            if (baseline.compareTo(BigDecimal.ZERO) > 0) {
                growthRate = forecastQty.subtract(baseline)
                        .divide(baseline, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)); // %
            }

            // 추천 조건
            boolean isHighForecast = growthRate.compareTo(BigDecimal.valueOf(10)) >= 0;  // +10% 이상 상승
            boolean isPositiveEvent = coef.compareTo(BigDecimal.ONE) > 0;                // 영향 계수 1.0 초과
            boolean isStockLow = forecastQty.compareTo(stock) > 0;                       // 재고 부족

            if ((isHighForecast || isPositiveEvent) && isStockLow) {
                BigDecimal recommended = forecastQty.subtract(stock);

                BIRecommendResult result = BIRecommendResult.builder()
                        .storeId(storeId)
                        .productId(forecast.getProductId())
                        .periodStartKey(forecast.getPeriodStartKey())
                        .periodEndKey(forecast.getPeriodEndKey())
                        .forecastQty(forecastQty)
                        .currentStockQty(stock)
                        .recommendedOrderQty(recommended)
                        .growthRate(growthRate)
                        .factorCoef(coef)
                        .calculatedAt(LocalDateTime.now())
                        .build();

                recommendRepository.save(result);
            }
        }
    }
}
