package com.youthcase.orderflow.util.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity // JPA 엔티티임을 명시
@Getter // Lombok: 모든 필드에 대한 Getter 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Lombok: 기본 생성자 (JPA 사용)
@Table(name = "NOTI_ITEM") // 매핑할 테이블 이름 지정
public class NotiItem {

    // NO -> ITEM_ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ID", nullable = false)
    private Long itemId;

    // 수신자 ID (필수 추가 항목)
    @Column(name = "RECEIVER_ID", nullable = false)
    private Long receiverId;

    // 알림헤더ID (NOTI_ID) -> NotiHeader와의 관계 설정
    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계 설정 (N개의 Item이 1개의 Header를 참조)
    @JoinColumn(name = "NOTI_ID", nullable = false) // FK 컬럼명 지정
    private NotiHeader header;

    // 내용 (CONTENT) -> 동적 데이터 또는 최종 알림 내용
    // 템플릿에 들어갈 JSON 형태의 동적 데이터(Payload)를 저장할 수 있도록 TEXT 타입 고려
    @Column(name = "CONTENT", columnDefinition = "TEXT", nullable = false)
    private String content;

    // 읽음 여부 (필수 추가 항목)
    @Column(name = "IS_READ", nullable = false)
    private Boolean isRead; // 또는 CHAR(1)을 사용하고 'Y', 'N'으로 관리할 수도 있습니다.

    // 알림 전송/생성 시간
    @Column(name = "SENT_AT", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    // --- 생성자/빌더 ---

    @Builder
    public NotiItem(NotiHeader header, Long receiverId, String content, Boolean isRead) {
        this.header = header;
        this.receiverId = receiverId;
        this.content = content;
        this.isRead = isRead != null ? isRead : false; // 기본값 false 설정
        this.sentAt = LocalDateTime.now(); // 생성 시점에 시간 자동 기록
    }

    // --- 비즈니스 로직 ---

    /**
     * 알림을 읽음 상태로 변경하는 메소드
     */
    /**
     * 알림을 읽음 상태로 변경하는 메소드
     */
    public void markAsRead() {
        if (!this.isRead) {
            this.isRead = true;
        }
    }
}