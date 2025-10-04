package com.youthcase.orderflow.sd.sdPayment.domain;

public enum PaymentMethod {
    CARD("card"),
    CASH("cash"),
    EASY("easy");

    private final String key;

    PaymentMethod(String key) {
        this.key = key;
    }
    public String getKey() {
        return key;
    }
}
