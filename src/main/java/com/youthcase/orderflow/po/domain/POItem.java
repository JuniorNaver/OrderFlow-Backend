package com.youthcase.orderflow.po.domain;

import com.youthcase.orderflow.master.price.domain.Price;
import com.youthcase.orderflow.master.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 🧾 발주 아이템 엔티티
 * - 장바구니(PR) 단계에서 수량 조정 가능
 * - 발주 확정(PO) 이후 수량 확정
 * - 출고(GI) 이후 출고량 및 미출량 자동 관리
 */
@Entity
@Table(name = "PO_ITEM")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"poHeader", "product"})
public class POItem {

    // ────────────────────────────────
    // 🔹 기본 키 및 연관관계
    // ────────────────────────────────
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "po_item_seq_gen")
    @SequenceGenerator(name = "po_item_seq_gen", sequenceName = "PO_ITEM_SEQ", allocationSize = 1)
    @Column(name = "PO_ITEM_NO", nullable = false)
    private Long itemNo;

    // 🔹 발주 헤더 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PO_ID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private POHeader poHeader;

    // 🔹 상품 코드 (FK → PRODUCT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GTIN", referencedColumnName = "GTIN", nullable = false)
    private Product product;

    // ────────────────────────────────
    // 🔹 수량 및 금액 필드
    // ────────────────────────────────
    @Column(name = "ORDER_QTY", nullable = false)
    private Long orderQty;

    @Column(name = "PENDING_QTY")
    private Long pendingQty;

    @Column(name = "SHIPPED_QTY")
    private Long shippedQty;

    // 해당 시점의 매입 단가 스냅샷
    @Column(name = "PURCHASE_PRICE", precision = 12, scale = 2, nullable = false)
    private BigDecimal purchasePrice;

    // ✅ 라인 금액 합계 (ORDER_QTY × PURCHASE_PRICE)
    @Column(name = "TOTAL", precision = 15, scale = 2)
    private BigDecimal total;

    // 예상 도착 일자
    @Column(name = "EXPECTED_ARRIVAL")
    private LocalDate expectedArrival;

    @Enumerated(EnumType.STRING)
    private POStatus status;

    // ────────────────────────────────
    // 🔹 자동 계산 훅
    // ────────────────────────────────
    @PrePersist
    public void calculateTotal() {
        if (purchasePrice != null && orderQty != null) {
            this.total = purchasePrice.multiply(BigDecimal.valueOf(orderQty));
        }
    }

    // ────────────────────────────────
    // 🔹 비즈니스 로직
    // ────────────────────────────────

    /**
     * 📦 발주 확정 (PR → PO 단계)
     * - 주문 수량 확정
     * - 미출 수량 초기화
     * - 출고 수량 0으로 초기화
     */
    public void confirmOrder(LocalDate confirmDate) {
        this.status = POStatus.PO;
        this.pendingQty = this.orderQty;
        this.shippedQty = 0L;
    }

    /**
     * 🚚 출고 처리 (PO → GI 단계)
     * @param shippedAmount 이번에 출고된 수량
     */
    public void shipItems(long shippedAmount) {
        if (this.status != POStatus.PO && this.status != POStatus.GI) {
            throw new IllegalStateException("출고는 발주 확정(PO) 상태에서만 가능합니다.");
        }
        if (this.pendingQty == null) this.pendingQty = this.orderQty;
        if (this.shippedQty == null) this.shippedQty = 0L;

        // 출고 수량 증가
        this.shippedQty += shippedAmount;

        // 남은 수량 감소
        this.pendingQty = Math.max(0, this.pendingQty - shippedAmount);

        // 상태 갱신
        if (this.pendingQty == 0) {
            this.status = POStatus.FULLY_RECEIVED; // 전량 출고 완료 시
        } else {
            this.status = POStatus.GI; // 일부 출고
        }
    }
}
