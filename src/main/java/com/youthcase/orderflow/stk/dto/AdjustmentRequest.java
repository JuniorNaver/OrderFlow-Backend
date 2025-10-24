package com.youthcase.orderflow.stk.dto;

import lombok.Data;
import java.util.List;

@Data
public class AdjustmentRequest {

    // 조정할 재고 항목 리스트
    private List<AdjustmentItem> items;

    @Data
    public static class AdjustmentItem {
        private Long lotId;           // 조정할 재고의 랏 ID
        private String productGtin;   // 제품 GTIN (선택 사항)
        private Long targetQuantity;   // ⭐️ 조정하고자 하는 최종 수량 (폐기에서는 'quantity'였으나, 조정에서는 목표 수량)
    }
}