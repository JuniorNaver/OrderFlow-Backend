package com.youthcase.orderflow.pr.domain;

public enum StorageMethod {
    ROOM_TEMP(2, "상온"),   // 상온: 2일
    COLD(1, "냉장"),       // 냉장: 1일
    FROZEN(2, "냉동"),     // 냉동: 2일
    OTHER(1, "기타");      // 기타: 1일

    private final int leadTimeDays;
    private final String displayName;

    StorageMethod(int leadTimeDays, String displayName) {
        this.leadTimeDays = leadTimeDays;
        this.displayName = displayName;
    }

    public int getLeadTimeDays() {
        return leadTimeDays;
    }

    public String getDisplayName() {
        return displayName;
    }
}