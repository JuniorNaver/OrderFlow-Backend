package com.youthcase.orderflow.pr.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Table(name = "PRODUCT",
  indexes = {
    @Index(name="IX_PRODUCT_KAN", columnList="KAN_CODE"),
    @Index(name="IX_PRODUCT_NAME", columnList="PRODUCT_NAME"),
    @Index(name="IX_PRODUCT_ORDERABLE", columnList="ORDERABLE")
  }
)
@Entity
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // 동등성은 GTIN 기준
public class Product {
    @Id
    @Column(name = "GTIN", length = 14, nullable = false)
    private String gtin;

    @Column(name = "PRODUCT_NAME", nullable = false, length = 100)
    private String productName;

    @Enumerated(EnumType.STRING)
    @Column(name = "UNIT", nullable = false, length = 20)
    private Unit unit;

    @Column(name = "PRICE", precision = 12, scale = 2, nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "STORAGE_METHOD", nullable = false, length = 20)
    private StorageMethod storageMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KAN_CODE", nullable = false, referencedColumnName = "KAN_CODE")
    private Category category;

    @Column(name="IMAGE_URL", length=230)
    private String imageUrl;

    @Column(name="DESCRIPTION", length=2000)
    private String description;

    @Column(name = "ORDERABLE", nullable = false)
    private Boolean orderable = Boolean.TRUE; // 기본값: 발주 가능

    // --- 치수(mm): 음수 방지 + NUMBER(6,0) 명시 ---
    @PositiveOrZero
    @Max(999_999)
    @Column(name = "WIDTH_MM",  precision = 6, scale = 0)
    private Integer widthMm;   // 가로

    @PositiveOrZero @Max(999_999)
    @Column(name = "DEPTH_MM",  precision = 6, scale = 0)
    private Integer depthMm;   // 세로(깊이)

    @PositiveOrZero @Max(999_999)
    @Column(name = "HEIGHT_MM", precision = 6, scale = 0)
    private Integer heightMm;  // 높이

    // 선택: 부피(cm^3) 계산 편의 메서드
    @Transient
    public java.math.BigDecimal getVolumeCubicCm() {
        if (widthMm == null || depthMm == null || heightMm == null) return null;
        BigDecimal mm3 = BigDecimal.valueOf(widthMm.longValue())
                .multiply(BigDecimal.valueOf(depthMm.longValue()))
                .multiply(BigDecimal.valueOf(heightMm.longValue()));
        // 1,000 mm^3 = 1 cm^3
        return mm3.divide(BigDecimal.valueOf(1000), 2, java.math.RoundingMode.HALF_UP);
    }

    // 한 번에 세팅
    public void setDimensionsMm(Integer width, Integer depth, Integer height) {
        this.widthMm = width;
        this.depthMm = depth;
        this.heightMm = height;
    }

    // 1) 가격 스케일 강제 (Lombok이 이 필드의 setter는 생성 안 함)
    public void setPrice(java.math.BigDecimal price) {
        this.price = (price == null) ? null : price.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    // 2) setter를 우회하는 경우 대비 (빌더/매퍼가 필드 직할로 넣을 때)
    @PrePersist @PreUpdate
    private void normalizePrice() {
        if (price != null) {
            price = price.setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }

}