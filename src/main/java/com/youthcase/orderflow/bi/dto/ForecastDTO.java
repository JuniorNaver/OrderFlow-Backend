/**
 * ForecastDTO
 * ------------
 * 📦 프론트엔드와의 데이터 교환을 위한 예측 결과 DTO.
 * - Entity 필드 중 필요한 데이터만 포함.
 */
package com.youthcase.orderflow.bi.dto;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ForecastDTO {

    /** 상품 식별자 */
    private Long productId;

    /** 기간 시작일 (YYYYMMDD) */
    private String periodStartKey;

    /** 기간 종료일 (YYYYMMDD) */
    private String periodEndKey;

    /** 예측 판매량 */
    private Double forecastQty;

    /** 모델 신뢰도(%) */
    private Double confidenceRate;
}
