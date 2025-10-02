package com.youthcase.orderflow.util.repository;

import com.youthcase.orderflow.util.domain.NotiItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotiItemRepository extends JpaRepository<NotiItem, Long> {

    /**
     * 특정 사용자의 모든 알림 아이템을 최신순으로 조회합니다.
     * * @param receiverId 알림 수신자 ID
     * @return 알림 아이템 리스트 (SentAt 기준으로 내림차순 정렬)
     */
    List<NotiItem> findByReceiverIdOrderBySentAtDesc(Long receiverId);

    /**
     * 특정 사용자의 읽지 않은 알림 아이템 개수를 조회합니다.
     * * @param receiverId 알림 수신자 ID
     * @param isRead 읽음 여부 (false)
     * @return 읽지 않은 알림 개수
     */
    long countByReceiverIdAndIsRead(Long receiverId, Boolean isRead);

    // 추가적으로 필요한 메서드:
    // List<NotiItem> findByReceiverIdAndIsReadOrderBySentAtDesc(Long receiverId, Boolean isRead);
}