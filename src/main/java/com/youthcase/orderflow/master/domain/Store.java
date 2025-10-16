package com.youthcase.orderflow.master.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "STORE_MASTER",
        indexes = {
                @Index(name = "IX_STORE_BRAND", columnList = "BRAND_CODE"),
                @Index(name = "IX_STORE_REGION", columnList = "REGION_CODE"),
                @Index(name = "IX_STORE_ACTIVE", columnList = "IS_ACTIVE")
        })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class Store {

    @Id
    @Column(name = "STORE_ID", length = 10, nullable = false)
    @Comment("지점 고유 식별자 (예: S0001)")
    private String storeId;

    @NotBlank
    @Size(max = 100)
    @Column(name = "STORE_NAME", length = 100, nullable = false)
    private String storeName;

    // --- 위치 정보 ---
    @Size(max = 200)
    @Column(name = "ADDR", length = 200)
    private String address;

    @Size(max = 200)
    @Column(name = "ADDR_DETAIL", length = 200)
    private String addressDetail;

    @Size(max = 10)
    @Column(name = "POST_CODE", length = 10)
    private String postCode;

    @Digits(integer = 4, fraction = 6) // 예: 127.123456
    @Column(name = "LONGITUDE", precision = 10, scale = 6)
    private BigDecimal longitude;

    @Digits(integer = 3, fraction = 6) // 예: 37.123456
    @Column(name = "LATITUDE", precision = 9, scale = 6)
    private BigDecimal latitude;

}