package com.youthcase.orderflow.gr.domain;

public enum GRExpiryType {
    NONE,       //유통기한 X
    FIXED_DAYS, //상품별 유통기한 일수 기반
    MANUAL,     //납품서에 직접 입력됨
    MFG_BASED   //제조일 기준(MFG_DATE+N일)
}
