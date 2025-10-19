package com.youthcase.orderflow.notification.dto;

import com.youthcase.orderflow.notification.domain.NotiHeader;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class NotiHeaderDTO {

    private final Long notiId;
    private final String type;
    private final Long stkId;
    private final String nav;
    private final LocalDateTime createdAt;

    // 엔티티를 DTO로 변환하는 팩토리 메서드
    public static NotiHeaderDTO from(NotiHeader header) {
        return NotiHeaderDTO.builder()
                .notiId(header.getNotiId())
                .type(header.getType())
                .stkId(header.getStkId())
                .nav(header.getNav())
                .createdAt(header.getCreatedAt())
                .build();
    }
}