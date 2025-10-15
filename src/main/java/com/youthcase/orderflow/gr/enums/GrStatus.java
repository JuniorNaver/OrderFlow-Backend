package com.youthcase.orderflow.gr.enums;

/**
 * 입고 상태 Enum
 */
public enum GrStatus {
    PENDING("입고 대기"),
    IN_PROGRESS("입고 진행중"),
    COMPLETED("입고 완료"),
    CANCELLED("입고 취소");

    private final String description;

    GrStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 완료된 상태인지 확인
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }

    /**
     * 취소 가능한 상태인지 확인
     */
    public boolean isCancellable() {
        return this == PENDING || this == IN_PROGRESS;
    }
}
