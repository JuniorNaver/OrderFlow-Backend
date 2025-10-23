package com.youthcase.orderflow.gr.status;

public enum LotStatus {
    ACTIVE,     //사용 가능한 정상 재고
    ON_HOLD,    //보류 중
    CONSUMED,   //이미 사용됨
    EXPIRED,    //유통기한 경과
    DISPOSED,   //폐기 완료
    RETURNED    //반품으로 회수된 재고
}