package com.youthcase.orderflow.sd.sdRefund.domain;

public enum RefundStatus {
    REQUESTED("환불 요청"),
    APPROVED("환불 승인"),
    COMPLETED("환불 완료"),
    REJECTED("환불 거절"),
    CANCELED("환불 취소"),
    FAILED("환불 실패");

    private final String description;

    RefundStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}