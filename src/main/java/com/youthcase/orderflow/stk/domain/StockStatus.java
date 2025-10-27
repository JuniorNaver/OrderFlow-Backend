package com.youthcase.orderflow.stk.domain;

import lombok.Getter;

/**
 * 📦 StockStatus — 재고 상태 정의
 * ------------------------------------------------------------
 * 재고(STK)의 전체 수명 주기(lifecycle)를 Enum으로 관리합니다.
 *
 * ✅ 상태 분류 기준
 *  1. 재고가 **창고 내에 존재하는가?**  → 용량/수량 집계 포함 여부
 *  2. 재고가 **판매/출고 가능한가?**  → ACTIVE 상태인지 여부
 *
 * ✅ 주요 상태 전이 흐름
 *  ACTIVE → NEAR_EXPIRY → EXPIRED → DISPOSED
 *        ↘ RETURNED (반품)
 *        ↘ EMPTY (수량 0, 일시적 비가용)
 *            ↘ ADJUSTED_TO_INACTIVE (조정 결과 비활성화)
 *        ↘ INACTIVE (관리 중지 상태)
 *
 * ✅ 용량/재고 합계 포함 기준
 *  - 포함: ACTIVE, NEAR_EXPIRY, EXPIRED, RETURNED
 *  - 제외: INACTIVE, DISPOSED, ADJUSTED_TO_INACTIVE, EMPTY
 */
@Getter
public enum StockStatus {

    /** ✅ 정상 재고 — 입고 또는 조정 후 정상 상태 */
    ACTIVE("활성 재고"),

    /** ⚙️ 비활성 재고 — 시스템상 관리 중지 또는 사용 불가 상태 (일반적 비가용 상태) */
    INACTIVE("비활성 재고"),

    /** ↩️ 반품 재고 — 매장에서 반품되어 임시 보관 중 */
    RETURNED("반품 재고"),

    /** 🗑️ 폐기 완료 — 완전히 폐기되어 창고에서 제거됨 */
    DISPOSED("폐기 완료"),

    /** 🚨 유통기한 경과 — 폐기 대상 (아직 창고에 존재함) */
    EXPIRED("유통기한 만료"),

    /** ⏳ 유통기한 임박 — 판매 가능하지만 주의 필요 */
    NEAR_EXPIRY("유통기한 임박"),

    /** 🧮 재고 조정 결과 비활성화됨 — 조정으로 인해 사용 중단된 상태 */
    ADJUSTED_TO_INACTIVE("조정으로 비활성화"),

    /** ❌ 일시적 비가용 — 수량 0 등의 이유로 검토 대상이 된 상태 (조정 전 단계) */
    EMPTY("재고 없음");

    private final String description;

    StockStatus(String description) {
        this.description = description;
    }
}
