/**
 * BI_FORECAST_RESULT 엔티티
 * --------------------------
 * 📊 예측 판매량 결과를 저장하는 BI 팩트 테이블.
 * - 단위 점포(Store) / 상품(Product) / 기간(Period) 기준으로 예측치와 신뢰도를 기록.
 * - ETL 배치 및 예측 모델 결과를 저장하는 핵심 테이블.
 */
package com.youthcase.orderflow.bi.domain.forecast;

import com.youthcase.orderflow.bi.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "BI_FORECAST_RESULT")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class BIForecastResult extends BaseTimeEntity {

    /** 예측 결과 고유 ID (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FORECAST_ID")
    private Long id;

    /** 점포 식별자 (FK: DIM_STORE.STORE_ID) */
    @Column(name = "STORE_ID", nullable = false)
    private Long storeId;

    /** 상품 식별자 (FK: DIM_PRODUCT.PRODUCT_ID) */
    @Column(name = "PRODUCT_ID", nullable = false)
    private Long productId;

    /** 예측 시작일 (YYYYMMDD 형식 키) */
    @Column(name = "PERIOD_START_KEY", length = 8)
    private String periodStartKey;

    /** 예측 종료일 (YYYYMMDD 형식 키) */
    @Column(name = "PERIOD_END_KEY", length = 8)
    private String periodEndKey;

    /** 예측 판매량 (모델이 산출한 값) */
    @Column(name = "FORECAST_QTY", precision = 10, scale = 2)
    private Double forecastQty;

    /** 모델의 신뢰도(%) */
    @Column(name = "CONFIDENCE_RATE", precision = 5, scale = 2)
    private Double confidenceRate;

    /** 사용된 예측 모델 버전명 */
    @Column(name = "MODEL_VERSION", length = 20)
    private String modelVersion;

    /** 예측 산출 일시 (BI 배치 완료 시각) */
    @Column(name = "CALCULATED_AT")
    private LocalDateTime calculatedAt;
}
