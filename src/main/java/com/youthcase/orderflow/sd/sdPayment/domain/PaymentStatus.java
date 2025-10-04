package com.youthcase.orderflow.sd.sdPayment.domain;

public enum PaymentStatus {
    REQUESTED("결제 요청"),
    APPROVED("결제 승인"),
    CANCELED("결제 취소"),
    FAILED("결제 실패");

    private final String description;

    PaymentStatus(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}
