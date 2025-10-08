/**
 * BIForecastRepository
 * ---------------------
 * 📚 BI_FORECAST_RESULT 테이블과의 JPA 데이터 접근 레이어.
 * - 기간별, 점포별, 상품별 예측 결과를 조회하는 쿼리를 정의.
 */
package com.youthcase.orderflow.bi.repository.forecast;

import com.youthcase.orderflow.bi.domain.forecast.BIForecastResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BIForecastRepository extends JpaRepository<BIForecastResult, Long> {

    /**
     * 특정 점포의 기간별 예측 결과를 조회한다.
     * @param storeId 점포 ID
     * @param from 시작일(YYYYMMDD)
     * @param to 종료일(YYYYMMDD)
     * @return 예측 결과 리스트
     */
    List<BIForecastResult> findByStoreIdAndPeriodStartKeyGreaterThanEqualAndPeriodEndKeyLessThanEqual(
            Long storeId, String from, String to
    );
}
