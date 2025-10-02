package com.youthcase.orderflow.util.service;

import com.youthcase.orderflow.util.domain.NotiItem;
import com.youthcase.orderflow.util.domain.NotiHeader;
import java.util.List;

public interface NotiItemService {

    /**
     * 특정 사용자에게 개별 알림 아이템을 생성하고 전송(저장)합니다.
     * @param header 참조할 알림 헤더 엔티티
     * @param receiverId 수신자 ID
     * @param content 알림 내용(동적 데이터)
     * @return 저장된 NotiItem 엔티티
     */
    NotiItem createItem(NotiHeader header, Long receiverId, String content);

    /**
     * 특정 사용자의 알림 목록을 최신순으로 조회합니다.
     * @param receiverId 수신자 ID
     * @return NotiItem 리스트
     */
    List<NotiItem> getNotificationsByReceiverId(Long receiverId);

    /**
     * 특정 알림 아이템을 '읽음' 상태로 변경합니다.
     * @param itemId 읽음 처리할 알림 아이템 ID
     */
    void markAsRead(Long itemId);

    /**
     * 특정 사용자의 읽지 않은 알림 개수를 조회합니다.
     * @param receiverId 수신자 ID
     * @return 읽지 않은 알림 개수
     */
    long getUnreadCount(Long receiverId);
}