package com.youthcase.orderflow.stk.dto;

import lombok.Data;
import java.util.List;

@Data
public class DisposalRequest {
    // 여러 개의 랏에 대한 폐기 요청을 담는 리스트
    private List<DisposalItem> items;

    @Data
    public static class DisposalItem {
        private Long lotId;           // 폐기할 재고의 랏 ID (Lot 테이블의 PK 또는 STK 테이블이 참조하는 ID)
        private String productGtin;   // 제품 GTIN (필수는 아니지만 검증에 유용)
        private Long quantity;         // 요청된 폐기 수량
    }
}