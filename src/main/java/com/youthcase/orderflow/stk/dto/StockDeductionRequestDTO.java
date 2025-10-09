package com.youthcase.orderflow.stk.dto;

import lombok.Data;
import java.util.List;

@Data
public class StockDeductionRequestDTO {
    // 실제 출고 요청 ID 또는 출고 오더 번호
    private String salesOrderId;

    // 출고할 항목들의 목록
    private List<DeductionItem> items;

    // 내부 DTO: 실제 재고 차감에 필요한 정보
    @Data
    public static class DeductionItem {
        private String gtin; // 상품 GTIN
        private String warehouseId; // 출고할 창고 ID
        private Integer quantity; // 출고 수량
        // 출고할 Lot ID를 명시할 수도 있습니다.
        // private Long lotId;
    }
}