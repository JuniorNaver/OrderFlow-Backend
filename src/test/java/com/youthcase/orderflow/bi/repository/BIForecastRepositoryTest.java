///**
// * BIForecastRepository 단위 테스트
// * --------------------------------
// * 📋 BI_FORECAST_RESULT 테이블의 CRUD 및 쿼리 동작 검증
// */
//package com.youthcase.orderflow.bi.repository;
//
//import com.youthcase.orderflow.bi.domain.forecast.BIForecastResult;
//import com.youthcase.orderflow.bi.repository.forecast.BIForecastRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//class BIForecastRepositoryTest {
//
//    @Autowired
//    private BIForecastRepository forecastRepository;
//
//    @Test
//    @DisplayName("특정 기간 내 예측 결과 조회 성공")
//    void findByStoreIdAndPeriodBetween() {
//        // given
//        BIForecastResult forecast = BIForecastResult.builder()
//                .storeId(1L)
//                .productId(1001L)
//                .periodStartKey("20251001")
//                .periodEndKey("20251007")
//                .forecastQty(BigDecimal.valueOf(125.5))
//                .confidenceRate(BigDecimal.valueOf(92.4))
//                .modelVersion("v1.0")
//                .calculatedAt(LocalDateTime.now())
//                .build();
//        forecastRepository.save(forecast);
//
//        // when
//        List<BIForecastResult> results =
//                forecastRepository.findByStoreIdAndPeriodStartKeyGreaterThanEqualAndPeriodEndKeyLessThanEqual(
//                        1L, "20251001", "20251031"
//                );
//
//        // then
//        assertThat(results).hasSize(1);
//        assertThat(results.get(0).getForecastQty()).isEqualTo(125.5);
//    }
//}
