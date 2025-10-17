package com.youthcase.orderflow.master.product.domain;

public enum Unit {
    EA("개"),     // 개
    BOX("박스"),    // 박스
    KG("킬로그램"),     // 킬로그램
    ML("리터");       // 리터
    // 필요하면 계속 추가 가능

    private final String displayName;

    Unit(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}