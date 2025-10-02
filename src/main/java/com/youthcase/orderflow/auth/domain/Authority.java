package com.youthcase.orderflow.auth.domain;

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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "AUTHORITY") // 매핑할 테이블 이름
public class Authority {

    // 권한ID (AUTHORITY_ID) - PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // NUMBER 타입이므로 일반적으로 IDENTITY 사용
    @Column(name = "AUTHORITY_ID", nullable = false)
    private Long authorityId;

    // 권한명 (AUTHORITY) - 권한을 식별하는 고유 이름 (예: "STK_WRITE", "ORDER_READ")
    @Column(name = "AUTHORITY", length = 100, nullable = false, unique = true)
    private String authority;

    // URL - 이 권한이 적용되는 리소스 경로 (예: "/api/stk/**", 또는 null)
    @Column(name = "URL", length = 255) // URL은 NULL 허용으로 가정
    private String url;

    // --- 생성자/빌더 ---

    @Builder
    public Authority(String authority, String url) {
        this.authority = authority;
        this.url = url;
    }

    // --- 비즈니스 로직 ---

    /**
     * 권한 정보를 업데이트하는 메소드 (관리자용)
     */
    public void updateAuthority(String authority, String url) {
        this.authority = authority;
        this.url = url;
    }
}