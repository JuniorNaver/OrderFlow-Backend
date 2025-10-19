package com.youthcase.orderflow.notification.service;

import com.youthcase.orderflow.notification.domain.NotiHeader;
import java.util.Optional;

public interface NotiHeaderService {

    /**
     * 새로운 알림 헤더를 생성하고 저장합니다.
     * @param type 알림 유형
     * @param stkId 알림 대상 객체 ID (STK_ID)
     * @param nav 네비게이션 경로
     * @return 저장된 NotiHeader 엔티티
     */
    NotiHeader createHeader(String type, Long stkId, String nav);

    /**
     * ID를 사용하여 알림 헤더를 조회합니다.
     * @param notiId 알림 헤더 ID
     * @return NotiHeader (Optional)
     */
    Optional<NotiHeader> findById(Long notiId);

    // 추후 필요할 경우, findByStkId 등의 메서드를 추가합니다.
}