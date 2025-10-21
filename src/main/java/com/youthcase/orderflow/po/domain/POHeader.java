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
    @Column(name = "TOTAL_AMOUNT", nullable = false)
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

    /** ✅ POHeader가 삭제되면 관련된 모든 POItem도 자동 삭제됨 */
    @OneToMany(
            mappedBy = "poHeader",
            cascade = CascadeType.ALL,     // 저장, 수정, 삭제 전파
            orphanRemoval = true           // 고아 객체(부모가 없어진 자식) 자동 삭제
    )
    private List<POItem> items = new ArrayList<>();
}