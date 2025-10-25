package com.youthcase.orderflow.po.domain;

import com.youthcase.orderflow.auth.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 📦 발주 헤더 엔티티
 * - 장바구니(PR), 저장(S), 발주(PO) 등 발주 단위의 상위 엔티티
 * - actionDate는 해당 상태(status)가 변경된 날짜를 의미함
 *   ex) PR → 10/24 생성, S → 10/25 저장, PO → 10/26 승인
 */
@Entity
@Table(name = "PO_HEADER")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"items", "user"})
public class POHeader {

    // 발주내역 ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "po_header_seq_gen")
    @SequenceGenerator(
            name = "po_header_seq_gen",           // JPA에서 사용할 이름
            sequenceName = "PO_HEADER_SEQ",       // DB에 만든 시퀀스 이름
            allocationSize = 1                    // 오라클에서는 보통 1로 맞춤
    )
    @Column(name = "PO_ID", nullable = false)
    private Long poId;

    // 상태 (PR: 발주 요청, PO: 발주 완료, S: 저장, D: 취소 등)
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 10, nullable = false)
    private POStatus status;

    // 매입 총액 (합계)
    @Column(name = "TOTAL_AMOUNT", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    // 요청/승인 일자
    @Column(name = "ACTION_DATE", nullable = false)
    private LocalDate actionDate;

    // 비고
    @Column(name = "REMARKS", length = 255)
    private String remarks;

    // 발주 생성자 계정 ID (USER 테이블 FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    // 바코드 번호(입고 스캔용)
    @Column(name = "EXTERNAL_ID", unique = true, length = 20)
    private String externalId;

    // 하위 발주 아이템 목록
    @OneToMany(mappedBy = "poHeader", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<POItem> items = new ArrayList<>();

    // ────────────────────────────────
    // 📘 도메인 행위 로직 (DDD 스타일)
    // ────────────────────────────────

    /** 발주 확정 (PR → PO) */
    public void confirmOrder() {
        this.status = POStatus.PO;
        this.actionDate = LocalDate.now();
    }

    /** 장바구니 저장 (PR → S) */
    public void saveAsCart(String remarks) {
        this.status = POStatus.S;
        this.remarks = remarks;
        this.actionDate = LocalDate.now();
    }
}
