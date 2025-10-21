package com.youthcase.orderflow.sd.sdRefund.domain;

import lombok.Getter;

@Getter
public enum RefundReason {
    DEFECTIVE("상품 불량"),
    EXPIRED("유통기한 문제"),
    CUSTOMER_CHANGE("단순 변심"),
    PAYMENT_ERROR("결제 오류"),
    DUPLICATE_PAYMENT("중복 결제"),
    OTHER("기타");

    private final String description;

    RefundReason(String description) {
        this.description = description;
    }
}
