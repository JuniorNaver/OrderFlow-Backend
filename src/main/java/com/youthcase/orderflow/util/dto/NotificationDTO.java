package com.youthcase.orderflow.util.dto;

import com.youthcase.orderflow.util.domain.NotiItem;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationDTO {

    private final Long itemId;          // NotiItem의 ID (개별 알림 PK)
    private final Long headerId;        // 참조하는 NotiHeader의 ID

    private final String type;          // 알림 유형 (Header에서 가져옴)
    private final String content;       // 알림 내용/메시지 (Item에서 가져옴)
    private final String navPath;       // 알림 클릭 시 이동 경로 (Header에서 가져옴)

    private final Boolean isRead;       // 읽음 여부 (Item에서 가져옴)
    private final LocalDateTime sentAt; // 전송 시간 (Item에서 가져옴)

    // NotiItem 엔티티를 DTO로 변환하는 팩토리 메서드
    public static NotificationDTO from(NotiItem item) {
        // NotiItem이 NotiHeader를 참조하고 있으므로, getHeader()를 통해 정보 통합
        return NotificationDTO.builder()
                .itemId(item.getItemId())
                // Header 정보
                .headerId(item.getHeader().getNotiId())
                .type(item.getHeader().getType())
                .navPath(item.getHeader().getNav())

                // Item 정보
                .content(item.getContent())
                .isRead(item.getIsRead())
                .sentAt(item.getSentAt())
                .build();
    }
}