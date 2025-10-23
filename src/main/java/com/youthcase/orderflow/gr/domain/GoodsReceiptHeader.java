package com.youthcase.orderflow.gr.domain;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.gr.status.GoodsReceiptStatus;
import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GR_HEADER")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GoodsReceiptHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gr_header_seq_gen")
    @SequenceGenerator(
            name = "gr_header_seq_gen",
            sequenceName = "GR_HEADER_SEQ",
            allocationSize = 1
    )
    @Column(name = "GR_HEADER_ID")
    private Long id;  // 입고내역ID (PK)

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    @Builder.Default
    private GoodsReceiptStatus status = GoodsReceiptStatus.RECEIVED;

    @Builder.Default
    @Column(name = "RECEIPT_DATE", nullable = false)
    private LocalDate receiptDate = LocalDate.now(); // 입고일자

    @Column(name = "NOTE", length = 255)
    private String note; // 비고

    // ✅ FK: 창고 (WAREHOUSE_MASTER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WAREHOUSE_ID", nullable = false)
    private Warehouse warehouse;

    // ✅ FK: 발주내역 (PO_HEADER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PO_ID", nullable = false)
    private POHeader poHeader;

    // ✅ FK: 계정 (APP_USER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    // ✅ 1:N 관계 - 입고 아이템
    @OneToMany(mappedBy = "header", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GoodsReceiptItem> items = new ArrayList<>();

}
