/**
 * BIForecastService 단위 테스트
 * ------------------------------
 * 🎯 Repository 호출 및 DTO 매핑 검증
 */
package com.youthcase.orderflow.bi.service;

import com.youthcase.orderflow.bi.domain.forecast.BIForecastResult;
import com.youthcase.orderflow.bi.dto.ForecastDTO;
import com.youthcase.orderflow.bi.mapper.BIForecastMapper;
import com.youthcase.orderflow.bi.repository.forecast.BIForecastRepository;
import com.youthcase.orderflow.bi.service.forecast.BIForecastService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class BIForecastServiceTest {

    @Mock
    private BIForecastRepository forecastRepository;

    @Mock
    private BIForecastMapper forecastMapper;

    @InjectMocks
    private BIForecastService forecastService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("DTO 매핑 포함 예측 결과 조회 성공")
    void getForecasts_success() {
        // given
        BIForecastResult entity = BIForecastResult.builder()
                .storeId(1L)
                .productId(1001L)
                .periodStartKey("20251001")
                .periodEndKey("20251007")
                .forecastQty(BigDecimal.valueOf(100.0))
                .confidenceRate(BigDecimal.valueOf(90.0))
                .modelVersion("v1.0")
                .calculatedAt(LocalDateTime.now())
                .build();

        ForecastDTO dto = ForecastDTO.builder()
                .productId(1001L)
                .periodStartKey("20251001")
                .forecastQty(100.0)
                .build();

        when(forecastRepository.findByStoreIdAndPeriodStartKeyGreaterThanEqualAndPeriodEndKeyLessThanEqual(1L, "20251001", "20251031"))
                .thenReturn(List.of(entity));
        when(forecastMapper.toDto(entity)).thenReturn(dto);

        // when
        List<ForecastDTO> results = forecastService.getForecasts(1L, "20251001", "20251031");

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getForecastQty()).isEqualTo(100.0);
    }
}
