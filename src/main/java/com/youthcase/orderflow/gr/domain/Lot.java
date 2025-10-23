package com.youthcase.orderflow.gr.domain;

import com.youthcase.orderflow.gr.status.LotStatus;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.product.domain.ExpiryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "LOT", indexes = {
        @Index(name="IX_LOT_PROD_EXP", columnList="GTIN, EXP_DATE"),
        @Index(name="IX_LOT_EXP", columnList="EXP_DATE"),
        @Index(name="IX_LOT_GR_ITEM", columnList="ITEM_NO")
})
@Getter
@Setter
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
    @Column(name = "LOT_ID")
    private Long lotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GTIN", referencedColumnName = "GTIN", nullable = true)
    private Product product;

    @Column(name = "EXP_DATE", nullable = true)
    private LocalDate expDate;

    @Column(name = "LOT_NO", length = 50, unique = true, nullable = false)
    private String lotNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "EXPIRY_TYPE", length = 16, nullable = false)
    private ExpiryType expiryType = ExpiryType.NONE;

    @Column(name = "QTY", nullable = false)
    private Long qty;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20, nullable = false)
    private LotStatus status = LotStatus.ACTIVE;

    @Column(name = "CREATED_AT", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private OffsetDateTime updatedAt;

    // FK: GoodsReceiptHeader
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_No", nullable = true)
    private GoodsReceiptItem goodsReceiptItem; // Java 컨벤션에 따라 소문자로 시작하도록 변경


    @Transient
    public long getRemainDays() {
        return ChronoUnit.DAYS.between(LocalDate.now(), expDate);
    }

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        if (this.createdAt == null) this.createdAt = now;
        this.updatedAt = now;

        if (this.lotNo == null || this.lotNo.isBlank()) {
            this.lotNo = String.format("LOT-%s-%s-%d",
                    LocalDate.now(),
                    product != null ? product.getGtin() : "UNKNOWN",
                    System.nanoTime());
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
