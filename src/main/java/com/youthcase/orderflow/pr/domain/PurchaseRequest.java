package com.youthcase.orderflow.pr.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "MM_PR") // 스키마 규칙에 맞춰 변경
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SequenceGenerator(name = "SEQ_MM_PR", sequenceName = "SEQ_MM_PR", allocationSize = 1)
public class PurchaseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MM_PR")
    private Long id;

    // 발주 점포
    @Column(name = "STORE_ID", length = 20, nullable = false)
    private String storeId;

    // 상품 식별자 (GTIN)
    @Column(name = "GTIN", length = 14, nullable = false)
    private String gtin;

    // 수량
    @Column(name = "QTY", nullable = false)
    private int qty;

    // 기대 입고일(선택)
    @Column(name = "EXPECTED_DATE")
    private LocalDate expectedDate;

    // 상태
    @Column(name = "STATUS", length = 20, nullable = false)
    private String status;

    // 발주자(선택) — 누가 발주했는지 추적하려면
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUESTER_ID") // FK → MM_USER.USER_ID
    private com.youthcase.orderflow.auth.domain.User requester;

    public static PurchaseRequest create(String storeId, String gtin, int qty, LocalDate expectedDate) {
        return PurchaseRequest.builder()
                .storeId(storeId)
                .gtin(gtin)
                .qty(qty)
                .expectedDate(expectedDate)
                .status("REQUESTED")
                .build();
    }

    public void approve() { this.status = "APPROVED"; }
    public void reject()  { this.status = "REJECTED"; }
}