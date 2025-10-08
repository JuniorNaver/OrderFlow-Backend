/**
 * RecommendDTO
 * -------------
 * 📦 프론트엔드로 전달할 추천 발주 결과 DTO.
 * - 기본 추천 수량 외에도 v2 로직에서의 추천 사유(상승률/영향계수)까지 함께 제공 가능.
 */
package com.youthcase.orderflow.bi.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class RecommendDTO {

    /** 상품 ID */
    private Long productId;

    /** 기간 시작일 (YYYYMMDD) */
    private String periodStartKey;

    /** 기간 종료일 (YYYYMMDD) */
    private String periodEndKey;

    /** 예측 판매량 */
    private BigDecimal forecastQty;

    /** 현재 재고 수량 */
    private BigDecimal currentStockQty;

    /** 추천 발주 수량 */
    private BigDecimal recommendedOrderQty;

    /** (옵션) 예측 상승률(%) - v2 추천 사유 */
    private BigDecimal growthRate;

    /** (옵션) 이벤트 영향 계수 - v2 추천 사유 */
    private BigDecimal factorCoef;
}
