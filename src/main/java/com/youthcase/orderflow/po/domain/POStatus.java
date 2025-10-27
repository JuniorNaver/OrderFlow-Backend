package com.youthcase.orderflow.po.domain;

public enum POStatus {
    PR,   // 발주 요청
    PO,   // 발주 완료
    S,    // 저장
    GI,    // 출고 처리
    PARTIAL_RECEIVED,  // ✅ 일부 입고 완료
    FULLY_RECEIVED,     // ✅ 전량 입고 완료
    CANCELED
}