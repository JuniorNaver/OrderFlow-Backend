package com.youthcase.orderflow.master.store.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 🏪 STORE_MASTER (지점 마스터)
 * - 지점(점포)의 기준정보 및 운영정보를 관리
 * - 관리자(Admin): 전체 필드 접근 가능 (등록/삭제/수정)
 * - 점장(Manager): 운영환경 관련 필드만 수정 가능
 */
@Entity
@Table(name = "STORE_MASTER",
        indexes = {
                @Index(name = "IX_STORE_BRAND", columnList = "BRAND_CODE"),
                @Index(name = "IX_STORE_REGION", columnList = "REGION_CODE"),
                @Index(name = "IX_STORE_ACTIVE", columnList = "IS_ACTIVE")
        })
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Store {

    // ────────────────────────────────
    // 🔹 기준정보 (관리자 전용)
    // ────────────────────────────────

    @Id
    @Column(name = "STORE_ID", length = 10, nullable = false)
    @Comment("지점 고유 식별자 (예: S0001)")
    private String storeId; // 시스템에서 자동 관리되는 지점 코드

    @NotBlank
    @Size(max = 100)
    @Column(name = "STORE_NAME", length = 100, nullable = false)
    @Comment("점포명")
    private String storeName; // 예: 청량리역점

    @NotBlank
    @Size(max = 10)
    @Column(name = "BRAND_CODE", length = 10, nullable = false)
    @Comment("브랜드 코드 (예: CU, GS25, 7E)")
    private String brandCode; // 본사 브랜드 구분

    @Column(name = "OPEN_DATE")
    @Comment("개점일 (점포 오픈일)")
    private LocalDate openDate;

    @Column(name = "CLOSE_DATE")
    @Comment("폐점일 (영업 종료일)")
    private LocalDate closeDate;

    @Size(max = 10)
    @Column(name = "REGION_CODE", length = 10)
    @Comment("지역 코드 (권역/지사 구분)")
    private String regionCode;

    @Size(max = 20)
    @Column(name = "MANAGER_ID", length = 20)
    @Comment("담당 관리자(본사 직원) ID")
    private String managerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "STORE_TYPE", length = 20)
    @Comment("점포 유형 (DIRECT: 직영, FRANCHISE: 가맹, SPECIAL: 특수입점 등)")
    private StoreType storeType;

    // ────────────────────────────────
    // 🔹 위치 정보 (관리자 전용, 점장은 수정 불가)
    // ────────────────────────────────

    @Size(max = 200)
    @Column(name = "ADDR", length = 200)
    @Comment("기본 주소")
    private String address;

    @Size(max = 200)
    @Column(name = "ADDR_DETAIL", length = 200)
    @Comment("상세 주소")
    private String addressDetail;

    @Size(max = 10)
    @Column(name = "POST_CODE", length = 10)
    @Comment("우편번호")
    private String postCode;

    @Digits(integer = 4, fraction = 6)
    @Column(name = "LONGITUDE", precision = 10, scale = 6)
    @Comment("경도 (Longitude)")
    private BigDecimal longitude;

    @Digits(integer = 3, fraction = 6)
    @Column(name = "LATITUDE", precision = 9, scale = 6)
    @Comment("위도 (Latitude)")
    private BigDecimal latitude;

    // ────────────────────────────────
    // 🔹 운영정보 (점장이 수정 가능)
    // ────────────────────────────────

    @Size(max = 50)
    @Column(name = "OWNER_NAME", length = 50)
    @Comment("점장명 (Manager Name) ✅ 점장 수정 가능")
    private String ownerName;

    @Size(max = 50)
    @Column(name = "BIZ_HOURS", length = 50)
    @Comment("영업시간 (예: 09:00~23:00) ✅ 점장 수정 가능")
    private String bizHours;

    @Size(max = 20)
    @Pattern(regexp = "^[0-9\\-+()]*$", message = "전화번호 형식이 올바르지 않습니다.")
    @Column(name = "CONTACT_NUMBER", length = 20)
    @Comment("점포 연락처 (예: 02-123-4567, 010-xxxx-xxxx) ✅ 점장 수정 가능")
    private String contactNumber;

    @Convert(converter = YNBooleanConverter.class)
    @Column(name = "IS_ACTIVE", length = 1, nullable = false)
    @Comment("운영 여부 (Y/N) ✅ 점장 수정 가능")
    private Boolean active;

    // ────────────────────────────────
    // 🔹 시스템 관리 컬럼 (자동)
    // ────────────────────────────────

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @Comment("등록 일시 (자동 생성)")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "UPDATED_AT", nullable = false)
    @Comment("수정 일시 (자동 갱신)")
    private LocalDateTime updatedAt;

    // ────────────────────────────────
    // 🔹 기본값 설정
    // ────────────────────────────────

    @PrePersist
    void onCreate() {
        if (active == null) active = true; // 기본값 Y
    }
}
