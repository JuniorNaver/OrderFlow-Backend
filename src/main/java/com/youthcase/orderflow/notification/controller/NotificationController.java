package com.youthcase.orderflow.notification.controller;

import com.youthcase.orderflow.notification.domain.NotiItem;
import com.youthcase.orderflow.notification.dto.NotificationDTO;
import com.youthcase.orderflow.notification.service.NotiItemService; // Service 계층 주입을 위해 import
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController // RESTful API 컨트롤러
@RequestMapping("/api/notifications") // 기본 URL 경로: /api/notifications
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성 (의존성 주입)
public class NotificationController {

    // NotiItemService 의존성 주입
    private final NotiItemService notiItemService;

    /**
     * [GET] 특정 사용자의 알림 목록 조회 (최신순)
     * GET /api/notifications/{receiverId}
     * * @param receiverId 알림을 조회할 사용자 ID
     * @return 알림 아이템 리스트 (List<NotiItem>)
     */
    @GetMapping("/{receiverId}")
    public ResponseEntity<List<NotificationDTO>> getNotifications(@PathVariable Long receiverId) {

        // 1. Service 계층에서 데이터베이스 엔티티 리스트를 조회합니다.
        List<NotiItem> items = notiItemService.getNotificationsByReceiverId(receiverId);

        // 2. 획득한 엔티티 리스트(items)를 DTO 리스트로 변환합니다. ⭐여기에 코드가 들어갑니다.⭐
        List<NotificationDTO> dtoList = items.stream()
                .map(NotificationDTO::from)
                .toList();

        // 3. 변환된 DTO 리스트를 클라이언트에게 응답합니다.
        // 메서드의 반환 타입도 List<NotiItem>에서 List<NotificationDTO>로 변경해야 합니다.
        return ResponseEntity.ok(dtoList);
    }

    /**
     * [GET] 특정 사용자의 읽지 않은 알림 개수 조회
     * GET /api/notifications/{receiverId}/unread-count
     *
     * @param receiverId 알림 수신자 ID
     * @return 읽지 않은 알림 개수 (JSON 객체)
     */
    @GetMapping("/{receiverId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long receiverId) {
        // Service 계층을 통해 읽지 않은 알림 개수 조회
        long count = notiItemService.getUnreadCount(receiverId);

        // Map.of를 사용하여 {"unreadCount": 5} 형태의 JSON을 반환
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    /**
     * [PUT] 특정 알림을 읽음 처리
     * PUT /api/notifications/{itemId}/read
     * * @param itemId 읽음 처리할 알림 아이템 ID
     * @return 204 No Content 응답
     */
    @PutMapping("/{itemId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long itemId) {

        // Service 계층의 비즈니스 로직(읽음 처리) 호출
        // 트랜잭션 처리는 NotiItemServiceImpl에서 담당합니다.
        notiItemService.markAsRead(itemId);

        // 성공적으로 처리되었으나 본문에 데이터를 담을 필요가 없을 때 204 No Content 반환
        return ResponseEntity.noContent().build();
    }
}