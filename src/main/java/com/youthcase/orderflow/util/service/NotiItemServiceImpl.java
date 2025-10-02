package com.youthcase.orderflow.util.service;

import com.youthcase.orderflow.util.domain.NotiItem;
import com.youthcase.orderflow.util.domain.NotiHeader;
import com.youthcase.orderflow.util.repository.NotiItemRepository;
import com.youthcase.orderflow.util.service.NotiItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotiItemServiceImpl implements NotiItemService {

    private final NotiItemRepository notiItemRepository;

    @Override
    @Transactional
    public NotiItem createItem(NotiHeader header, Long receiverId, String content) {
        NotiItem newItem = NotiItem.builder()
                .header(header)
                .receiverId(receiverId)
                .content(content)
                .build();

        return notiItemRepository.save(newItem);
    }

    @Override
    public List<NotiItem> getNotificationsByReceiverId(Long receiverId) {
        // Repository의 쿼리 메서드 사용
        return notiItemRepository.findByReceiverIdOrderBySentAtDesc(receiverId);
    }

    @Override
    @Transactional // 읽음 처리는 데이터 변경이므로 쓰기 트랜잭션 필요
    public void markAsRead(Long itemId) {
        // 1. 아이템 조회 (없으면 예외 발생)
        NotiItem item = notiItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Notification Item not found with ID: " + itemId));

        // 2. 도메인(Entity)의 비즈니스 로직 호출
        item.markAsRead();
        // save() 호출 없이 트랜잭션 종료 시 Dirty Checking에 의해 자동 업데이트
    }

    @Override
    public long getUnreadCount(Long receiverId) {
        // Repository의 쿼리 메서드 사용
        return notiItemRepository.countByReceiverIdAndIsRead(receiverId, false);
    }
}