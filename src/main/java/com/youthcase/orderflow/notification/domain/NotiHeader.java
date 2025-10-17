package com.youthcase.orderflow.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity // Jakarta Persistence: 엔티티임을 명시
@Getter // Lombok: 모든 필드에 대한 Getter 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Lombok: 기본 생성자 (JPA 사용)
@Table(name = "NOTI_HEADER") // NotiItem이 참조하는 테이블명
public class NotiHeader {

    // 알림헤더ID (NOTI_ID)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID 자동 생성 전략 사용
    @Column(name = "NOTI_ID", nullable = false) // NotiItem의 @JoinColumn(name = "NOTI_ID")와 일치해야 함
    private Long notiId;

    // 종류 (TYPE) - 알림 유형
    @Column(name = "TYPE", length = 50, nullable = false)
    private String type;

    // TARGET_ID (재고 STK_ID) - 알림 대상 객체의 ID
    @Column(name = "STK_ID", nullable = false)
    private Long stkId;

    // 네비게이션 (NAV) - 클릭 시 이동할 경로/화면
    @Column(name = "NAV", length = 255, nullable = false)
    private String nav;

    // 헤더 생성 시간 (추가)
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // --- 생성자/빌더 ---

    @Builder
    public NotiHeader(String type, Long stkId, String nav) {
        this.type = type;
        this.stkId = stkId;
        this.nav = nav;
        this.createdAt = LocalDateTime.now(); // 생성 시점에 시간 자동 기록
    }

    // --- 비즈니스 로직 ---
}