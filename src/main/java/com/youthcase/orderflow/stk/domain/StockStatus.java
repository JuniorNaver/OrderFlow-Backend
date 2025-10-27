package com.youthcase.orderflow.stk.domain.enums;

import lombok.Getter;

@Getter
public enum StockStatus {
    ACTIVE("활성 재고"),
    INACTIVE("비활성 재고"),
    RETURNED("반품 재고"),
    DISPOSED("폐기 완료"),
    EXPIRED("유통기한 만료"),
    NEAR_EXPIRY("유통기한 임박"),
    ADJUSTED_TO_INACTIVE("조정으로 비활성화"),
    EMPTY("재고 없음");

    private final String description;

    StockStatus(String description) {
        this.description = description;
    }
}
