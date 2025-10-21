package com.youthcase.orderflow.auth.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 시스템 고정 권한 Enum
 * - 관리자, 점장, 점원 등 모든 사용자에게 공통 적용되는 핵심 권한
 * - DB authority 테이블에 미리 동기화됨
 */
@Getter
@RequiredArgsConstructor
public enum AuthorityType {

    STK_READ("STK_READ", "/api/stk/**", "재고 조회 권한"),
    STK_WRITE("STK_WRITE", "/api/stk/**", "재고 수정 권한"),
    BI_VIEW("BI_VIEW", "/api/bi/**", "BI 대시보드 접근"),
    PR_ORDER("PR_ORDER", "/api/pr/**", "발주 요청 권한"),
    GR_RECEIVE("GR_RECEIVE", "/api/gr/**", "입고 처리 권한"),
    ENVIRONMENT_EDIT("ENVIRONMENT_EDIT", "/api/settings/**", "점포 운영환경 수정 권한"),
    SD("SD", "/api/sd/**", "판매관리");

    private final String authority;   // 권한명
    private final String urlPattern;  // 적용 URL
    private final String description; // 설명
}
