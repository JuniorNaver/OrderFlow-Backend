package com.youthcase.orderflow.master.domain;

public enum StorageMethod {
    ROOM_TEMP(2, "실온"),   // 상온: 2일
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

    /** zone/totalCategory 등 다양한 입력을 enum으로 정규화 */
    public static StorageMethod fromInput(String s) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException("zone is required");
        String v = s.trim().toLowerCase();
        return switch (v) {
            // 영어 키워드
            case "room", "room_temp" -> ROOM_TEMP;
            case "chilled", "cold"   -> COLD;
            case "frozen"            -> FROZEN;
            case "other"             -> OTHER;
            // 한글 라벨
            case "실온" -> ROOM_TEMP;
            case "냉장" -> COLD;
            case "냉동" -> FROZEN;
            case "기타" -> OTHER;
            default -> throw new IllegalArgumentException("zone must be one of room|chilled|frozen|other");
        };
    }
}