package com.youthcase.orderflow.gr.enums;

/**
 * LOT 상태 Enum
 */
public enum LotStatus {
    ACTIVE("정상"),
    EXPIRING_SOON("유통기한 임박"),
    EXPIRED("유통기한 만료"),
    SOLD_OUT("판매 완료"),
    DISCARDED("폐기");

    private final String description;

    LotStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 정상 상태인지 확인
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * 판매 가능한 상태인지 확인
     */
    public boolean isSaleable() {
        return this == ACTIVE || this == EXPIRING_SOON;
    }

    /**
     * 만료된 상태인지 확인
     */
    public boolean isExpired() {
        return this == EXPIRED;
    }

    /**
     * 폐기된 상태인지 확인
     */
    public boolean isDiscarded() {
        return this == DISCARDED;
    }
}
