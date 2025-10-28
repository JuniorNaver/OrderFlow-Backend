package com.youthcase.orderflow.auth.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor; // Builder 사용을 위한 AllArgsConstructor 추가

import java.time.LocalDateTime;

@Entity
@Table(name = "PASSWORD_RESET_TOKEN") // 토큰을 저장할 테이블 이름
@Getter
@Builder // 클래스 레벨 빌더 사용
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE) // 빌더를 통한 객체 생성을 유도
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 실제 비밀번호 초기화에 사용될 토큰 값 (UUID 형태)
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    // 💡 변경: String userId 대신 User 엔티티를 직접 참조하는 ManyToOne 관계
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user; // User 엔티티 직접 참조 (필드명: user)

    // 토큰 만료 시간
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    // 토큰 사용 여부 플래그
    @Column(name = "used", nullable = false)
    @Builder.Default // 빌더 패턴 사용 시 초기값을 false로 설정
    private boolean used = false;


    /**
     * 토큰 사용 완료 처리
     */
    public void useToken() {
        this.used = true;
    }

    /**
     * 토큰 만료 여부 확인
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }


}