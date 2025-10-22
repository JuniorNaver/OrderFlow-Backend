package com.youthcase.orderflow.gr.status;

public enum GoodsReceiptStatus {
    DRAFT,        // 작성중
    RECEIVED,     // 입고검수 완료 (아직 확정 X)
    CONFIRMED,    // 입고확정 → 재고반영 완료
    CANCELED      // 취소
}
