package com.youthcase.orderflow.gr.status;

public enum GRExpiryType {
    NONE,       //유통기한 X
    FIXED_DAYS, //입고일(SYSDATE) + SHELF_LIFE_DAYS
    MANUAL,     //유통기한 수동입력: 납품서에 직접 입력됨
    MFG_BASED   //제조일(바코드에 포함) + SHELF_LIFE_DAYS
}
