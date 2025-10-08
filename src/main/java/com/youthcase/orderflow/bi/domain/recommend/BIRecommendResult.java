/**
 * BI_RECOMMEND_RESULT 엔티티
 * --------------------------
 * 🛒 예측 판매량 기반 추천 발주 결과 테이블.
 * - 점포별 / 상품별 / 기간별 추천 발주 수량을 저장.
 * - PR(발주 요청) 화면에서 “추천 발주 리스트”로 제공.
 * - v2 로직에서는 예측 상승률(GROWTH_RATE), 이벤트 영향계수(FACTOR_COEF)도 기록하여 추천 사유 제공.
 */
package com.youthcase.orderflow.bi.domain.recommend;

import com.youthcase.orderflow.bi.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "BI_RECOMMEND_RESULT",
        indexes = {
                @Index(name = "IX_RECOMMEND_STORE_PRODUCT", columnList = "STORE_ID, PRODUCT_ID"),
                @Index(name = "IX_RECOMMEND_PERIOD", columnList = "PERIOD_START_KEY, PERIOD_END_KEY")
        }
)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class BIRecommendResult extends BaseTimeEntity {

    /** 추천 결과 ID (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RECOMMEND_ID")
    private Long id;

    /** 점포 ID (FK: DIM_STORE.STORE_ID) */
    @Column(name = "STORE_ID", nullable = false)
    private Long storeId;

    /** 상품 ID (FK: DIM_PRODUCT.PRODUCT_ID) */
    @Column(name = "PRODUCT_ID", nullable = false)
    private Long productId;

    /** 예측 기간 시작일 (YYYYMMDD) */
    @Column(name = "PERIOD_START_KEY", length = 8, nullable = false)
    private String periodStartKey;

    /** 예측 기간 종료일 (YYYYMMDD) */
    @Column(name = "PERIOD_END_KEY", length = 8, nullable = false)
    private String periodEndKey;

    /** 예측 판매량 */
    @Column(name = "FORECAST_QTY", precision = 10, scale = 2)
    private BigDecimal forecastQty;

    /** 현재 재고 수량 */
    @Column(name = "CURRENT_STOCK_QTY", precision = 10, scale = 2)
    private BigDecimal currentStockQty;

    /** 추천 발주 수량 (예측 판매량 - 현재 재고 수량) */
    @Column(name = "RECOMMENDED_ORDER_QTY", precision = 10, scale = 2)
    private BigDecimal recommendedOrderQty;

    /** 계산 일시 (추천 산출 시각) */
    @Column(name = "CALCULATED_AT")
    private LocalDateTime calculatedAt;

    /**
     * v2 신규: 추천 사유 제공 컬럼
     * - GROWTH_RATE: 예측 대비 과거 평균 판매량 증가율 (%)
     * - FACTOR_COEF: 이벤트 영향 계수 (1.0 이상이면 상향 보정)
     */
    @Column(name = "GROWTH_RATE", precision = 6, scale = 2)
    private BigDecimal growthRate;

    @Column(name = "FACTOR_COEF", precision = 5, scale = 2)
    private BigDecimal factorCoef;
}
