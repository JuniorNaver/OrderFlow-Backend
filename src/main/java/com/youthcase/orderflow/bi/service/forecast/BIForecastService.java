/**
 * BIForecastService
 * ------------------
 * 🧩 예측 결과 비즈니스 로직 담당 서비스.
 * - Repository를 통해 DB 접근.
 * - Mapper를 통해 DTO 변환.
 */
package com.youthcase.orderflow.bi.service.forecast;

import com.youthcase.orderflow.bi.dto.ForecastDTO;
import com.youthcase.orderflow.bi.mapper.BIForecastMapper;
import com.youthcase.orderflow.bi.repository.forecast.BIForecastRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BIForecastService {

    private final BIForecastRepository repository;
    private final BIForecastMapper mapper;

    /**
     * 특정 점포의 기간별 예측 결과를 조회한다.
     * @param storeId 점포 ID
     * @param from 시작일(YYYYMMDD)
     * @param to 종료일(YYYYMMDD)
     * @return ForecastDTO 리스트
     */
    public List<ForecastDTO> getForecasts(Long storeId, String from, String to) {
        return repository.findByStoreIdAndPeriodStartKeyGreaterThanEqualAndPeriodEndKeyLessThanEqual(storeId, from, to)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
