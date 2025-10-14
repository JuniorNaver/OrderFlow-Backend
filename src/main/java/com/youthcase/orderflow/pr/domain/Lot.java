package com.youthcase.orderflow.pr.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "LOT", indexes = {
        @Index(name="IX_LOT_PROD_EXP", columnList="GTIN, EXP_DATE"),
        @Index(name="IX_LOT_EXP", columnList="EXP_DATE"),
        @Index(name="IX_LOT_GR", columnList="GR_ID")
})
@Getter
@Setter
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
    @Column(name = "LOT_ID")
    private Long lotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GTIN", nullable = false)
    private Product product;

    @Column(name = "EXP_DATE", nullable = false)
    private LocalDate expDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "EXPIRY_TYPE", length = 16, nullable = false)
    private ExpiryType expiryType;

    @jakarta.validation.constraints.PositiveOrZero
    @Column(name = "QTY", precision = 12, scale = 2, nullable = false)
    private BigDecimal qty;

    public enum LotStatus { ACTIVE, ON_HOLD, CONSUMED, EXPIRED, DISPOSED, RETURNED }
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20, nullable = false)
    private LotStatus status = LotStatus.ACTIVE;

    @Column(name = "CREATED_AT", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private OffsetDateTime updatedAt;

    // FK: GR_HEADER
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GR_ID", nullable = false)
    private GRHeader grHeader;

    @Transient
    public long getRemainDays() {
        return ChronoUnit.DAYS.between(LocalDate.now(), expDate);
    }

    @PrePersist
    void onCreate() {
        var now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
  }
