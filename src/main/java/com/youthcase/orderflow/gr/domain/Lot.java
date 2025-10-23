package com.youthcase.orderflow.gr.domain;

import com.youthcase.orderflow.gr.status.LotStatus;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.product.domain.ExpiryType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "LOT", indexes = {
        @Index(name = "IX_LOT_PROD_EXP", columnList = "GTIN, EXP_DATE"),
        @Index(name = "IX_LOT_EXP", columnList = "EXP_DATE"),
        @Index(name = "IX_LOT_GR_ITEM", columnList = "ITEM_NO")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOT_ID")
    private Long lotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GTIN", referencedColumnName = "GTIN", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_NO")
    private GoodsReceiptItem goodsReceiptItem;

    @Column(name = "LOT_NO", unique = true, nullable = false, length = 50)
    private String lotNo;

    @Column(name = "MFG_DATE", nullable = true)
    private LocalDate mfgDate;

    @Column(name = "EXP_DATE", nullable = true)
    private LocalDate expDate;

    @Column(name = "QTY", nullable = false)
    private Long qty; // ✅ 실제 수량의 주체

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20, nullable = false)
    @Builder.Default
    private LotStatus status = LotStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "EXPIRY_TYPE", length = 16, nullable = false)
    @Builder.Default
    private ExpiryType expiryType = ExpiryType.NONE;

    @Column(name = "CREATED_AT", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private OffsetDateTime updatedAt;

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

    @Transient
    public long getRemainDays() {
        return expDate == null ? 0 : ChronoUnit.DAYS.between(LocalDate.now(), expDate);
    }
}
