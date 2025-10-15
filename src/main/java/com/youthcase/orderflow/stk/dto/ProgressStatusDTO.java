package com.youthcase.orderflow.stk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProgressStatusDTO {
    /**
     * 카드의 제목 (예: "창고 적재 용량 현황", "유통기한 임박 현황")
     */
    private String title;

    /**
     * 전체 기준 값 (예: 총 창고 용량 1000 CBM, 총 재고 수량 5000 개)
     */
    private Long total;

    /**
     * 현재 사용 값 또는 위험 값 (예: 사용 중인 용량 780 CBM, 임박 재고 수량 1275 개)
     */
    private Long current;

    /**
     * 데이터의 단위 (예: "CBM", "개", "팔레트")
     */
    private String unit;
}