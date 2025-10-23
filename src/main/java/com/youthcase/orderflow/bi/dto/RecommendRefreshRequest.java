/**
 * RecommendRefreshRequest
 * ------------------------
 * 🔁 추천 발주 갱신 요청 바디 DTO.
 * - v1: stockData만 필요
 * - v2: baselineData(기준 판매량), factorCoefData(영향계수) 추가 필요
 */
package com.youthcase.orderflow.bi.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class RecommendRefreshRequest {

    /** 점포 ID */
    private String storeId;

    /** 기간 시작일 (YYYYMMDD) */
    private String from;

    /** 기간 종료일 (YYYYMMDD) */
    private String to;

    /** 재고 정보: <ProductId, 재고수량> */
    private Map<Long, BigDecimal> stockData;

    /** 전월 실적/평균 판매량: <ProductId, 기준수량> */
    private Map<Long, BigDecimal> baselineData;

    /** 이벤트 영향 계수: <ProductId, 계수(1.0=중립)> */
    private Map<Long, BigDecimal> factorCoefData;
}
