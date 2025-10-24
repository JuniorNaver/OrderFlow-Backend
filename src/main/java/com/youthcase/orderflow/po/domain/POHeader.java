package com.youthcase.orderflow.po.domain;

import com.youthcase.orderflow.auth.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PO_HEADER")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class POHeader {

    // 발주내역 ID (PK)
    @Id
    @Column(name = "PO_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "po_header_seq_gen")
    @SequenceGenerator(
            name = "po_header_seq_gen",           // JPA에서 사용할 이름
            sequenceName = "PO_HEADER_SEQ",       // DB에 만든 시퀀스 이름
            allocationSize = 1                    // 오라클에서는 보통 1로 맞춤
    )
    private Long poId;

    // 상태(PR: 발주 요청, PO: 발주 완료, D: 삭제/취소)
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 10, nullable = false)
    private POStatus status;

    // 매입총액
    @Column(name = "TOTAL_AMOUNT")
    private Long totalAmount;

    // 요청/승인 일자
    @Column(name = "ACTION_DATE", nullable = false)
    private LocalDate actionDate;

    // 비고
    @Column(name = "REMARKS", length = 255)
    private String remarks;

    // 계정 ID (USER 테이블 FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    // 바코드 번호(입고 스캔용)
    @Column(name = "EXTERNAL_ID", unique = true, length = 20)
    private String externalId;

    @OneToMany(mappedBy = "poHeader", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<POItem> items = new ArrayList<>();


}